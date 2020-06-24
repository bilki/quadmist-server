package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.common.domain.Color.{Blue, Red}
import com.lambdarat.quadmist.common.domain.{Card, Player}
import com.lambdarat.quadmist.common.game.GameError.{
  InvalidTransition,
  MultipleErrors,
  NotOwnedCard,
  PlayerAlreadyJoined,
  PlayerNeverJoined,
  PlayerSlotsFull
}
import com.lambdarat.quadmist.common.game.GameEvent.{
  GameFinished,
  PlayerHand,
  PlayerJoined,
  PlayerMove
}
import com.lambdarat.quadmist.common.game.GamePhase.{Finish, Initial, PlayerBlueTurn, PlayerRedTurn}
import com.lambdarat.quadmist.common.game.{GameError, GameEvent, InitialHand}
import com.lambdarat.quadmist.common.utils.Identified
import com.lambdarat.quadmist.common.utils.Identified._
import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.QuadmistCommon._

import fs2.{Pipe, Stream}
import monocle.macros.GenLens

import cats.data.Validated._
import cats.data._
import cats.effect.Sync
import cats.implicits._

trait GameStateMachine[F[_]] {
  def transition(
      playerId: Player.Id,
      gameVar: Game[F],
      turns: Turns[F]
  ): Pipe[F, GameEvent, Unit]
}

object GameStateMachine {
  def apply[F[_]](implicit sm: GameStateMachine[F]): GameStateMachine[F] = sm

  def playerJoined(gameInfo: GameInfo, playerId: Player.Id): Either[GameError, GameInfo] =
    (gameInfo.playerOne, gameInfo.playerTwo) match {
      case (Some(_), Some(_))              => PlayerSlotsFull.asLeft
      case (Some(p1), _) if p1 == playerId => PlayerAlreadyJoined(playerId).asLeft
      case (_, Some(p2)) if p2 == playerId => PlayerAlreadyJoined(playerId).asLeft
      case (Some(_), None)                 => gameInfo.copy(playerTwo = playerId.some).asRight
      case (None, _)                       => gameInfo.copy(playerOne = playerId.some).asRight
    }

  private lazy val redHandLens  = GenLens[GameInfo](_.state.current.board.redHand)
  private lazy val blueHandLens = GenLens[GameInfo](_.state.current.board.blueHand)

  def isFromPlayer(
      playerId: Player.Id,
      cards: List[Identified[Card.Id, Card]]
  )(id: Card.Id): ValidatedNel[GameError, Card] =
    cards
      .find(_.id == id)
      .fold[ValidatedNel[GameError, Card]](invalidNel(NotOwnedCard(id, playerId)))(cardWithId =>
        validNel(cardWithId.entity)
      )

  def validateHand(
      hand: InitialHand,
      playerId: Player.Id,
      cards: List[Identified[Card.Id, Card]]
  ): ValidatedNel[GameError, Set[Card]] = {
    val validate = isFromPlayer(playerId, cards) _

    (validate(hand.c1), validate(hand.c2), validate(hand.c3), validate(hand.c4), validate(hand.c5))
      .mapN(Set(_, _, _, _, _))
  }

  def playerHand(
      gameInfo: GameInfo,
      playerHand: PlayerHand,
      playerId: Player.Id,
      playerCards: List[Identified[Card.Id, Card]]
  ): Either[GameError, GameInfo] = {
    // Player one is RED, player two is BLUE
    val isRed  = gameInfo.playerOne.contains(playerId)
    val isBlue = gameInfo.playerTwo.contains(playerId)
    val hand   = playerHand.initialHand

    validateHand(hand, playerId, playerCards).toEither
      .fold(
        errors => MultipleErrors(errors).asLeft,
        { validatedHand =>
          if (isBlue) blueHandLens.set(validatedHand)(gameInfo).asRight
          else if (isRed) redHandLens.set(validatedHand)(gameInfo).asRight
          else PlayerNeverJoined(playerId).asLeft
        }
      )
  }

  def chooseNextTransition[F[_]: Sync: GameRepository](
      gameInfo: GameInfo,
      event: Identified[Player.Id, GameEvent]
  ): F[GameInfo] =
    (gameInfo.phase, event.entity) match {
      case (Initial, PlayerJoined)                                       =>
        Sync[F].fromEither(playerJoined(gameInfo, event.id))
      case (Initial, ph: PlayerHand)                                     =>
        for {
          playerCards <- GameRepository[F].getCardsBy(event.id)
          newGameInfo <- Sync[F].fromEither(playerHand(gameInfo, ph, event.id, playerCards))
        } yield newGameInfo
      case (PlayerBlueTurn, turn: PlayerMove) if turn.move.color == Blue => ???
      case (PlayerRedTurn, turn: PlayerMove) if turn.move.color == Red   => ???
      case (PlayerRedTurn | PlayerBlueTurn, GameFinished)                => ???
      case (Finish, _)                                                   => ???
      case _                                                             =>
        Sync[F].raiseError(InvalidTransition(event.entity, gameInfo.phase))
    }

  implicit def stateMachine[F[_]: Sync: GameRepository]: GameStateMachine[F] =
    new GameStateMachine[F] {
      override def transition(
          playerId: Player.Id,
          gameVar: Game[F],
          turns: Turns[F]
      ): Pipe[F, GameEvent, Unit] =
        gameEvents =>
          for {
            gameInfo        <- gameVar.take.toStream
            event           <- gameEvents
            gameInfoUpdated <- chooseNextTransition(gameInfo, event.withId(playerId))
                                 .attemptNarrow[GameError]
                                 .map(_.leftMap(_.withId(playerId)))
                                 .toStream
            _               <- Stream.emit(gameInfoUpdated.map(_.state.current))
                                 .through(turns.publish)
            _               <- gameVar.put(gameInfoUpdated.getOrElse(gameInfo)).toStream
          } yield ()
    }
}
