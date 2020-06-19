package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Common.Color.{Blue, Red}
import com.lambdarat.quadmist.game.GameError.{InvalidTransition, PlayerSlotsFull}
import com.lambdarat.quadmist.game.GameEvent.{
  GameFinished,
  PlayerJoined,
  PlayerMove,
  PlayerRequestHand
}
import com.lambdarat.quadmist.game.GamePhase.{Finish, Initial, PlayerBlueTurn, PlayerRedTurn}
import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.QuadmistCommon._

import fs2.{Pipe, Stream}

import cats.effect.Sync
import cats.implicits._

trait GameStateMachine[F[_]] {
  def transition(
      gameInfoVar: Game[F],
      topic: Turns[F]
  ): Pipe[F, GameEvent, Unit]
}

object GameStateMachine {
  def apply[F[_]](implicit sm: GameStateMachine[F]): GameStateMachine[F] = sm

  def playerJoined[F[_]: Sync: GameRepository](
      gameInfo: GameInfo,
      pj: PlayerJoined
  ): F[GameInfo] = {
    val nextGameInfo = (gameInfo.playerOne, gameInfo.playerTwo) match {
      case (Some(_), Some(_)) => (PlayerSlotsFull: GameError).asLeft
      case (Some(_), None)    => gameInfo.copy(playerTwo = pj.id.some).asRight
      case (None, _)          => gameInfo.copy(playerOne = pj.id.some).asRight
    }

    Sync[F].fromEither(nextGameInfo)
  }

  def chooseNextTransition[F[_]: Sync: GameRepository](
      gameInfo: GameInfo,
      event: GameEvent
  ): F[GameInfo] =
    (gameInfo.phase, event) match {
      case (Initial, pj: PlayerJoined)                                   =>
        playerJoined(gameInfo, pj)
      case (Initial, ph: PlayerRequestHand)                              => ???
      case (PlayerBlueTurn, turn: PlayerMove) if turn.move.color == Blue => ???
      case (PlayerRedTurn, turn: PlayerMove) if turn.move.color == Red   => ???
      case (PlayerRedTurn | PlayerBlueTurn, GameFinished)                => ???
      case (Finish, _)                                                   => ???
      case _                                                             =>
        Sync[F].raiseError(InvalidTransition(gameInfo, event))
    }

  implicit def stateMachine[F[_]: Sync: GameRepository]: GameStateMachine[F] =
    new GameStateMachine[F] {
      override def transition(
          gameVar: Game[F],
          turns: Turns[F]
      ): Pipe[F, GameEvent, Unit] =
        gameEvents =>
          for {
            gameInfo        <- gameVar.take.toStream
            event           <- gameEvents
            f                = Sync[F]
            gameInfoUpdated <- chooseNextTransition(gameInfo, event).handleErrorWith {
                                 case gameError: GameError =>
                                   for {
                                     _ <- gameVar.put(gameInfo)
                                     _ <- f.raiseError[Unit](gameError) // TODO send to topic...
                                   } yield gameInfo
                               }.toStream
            _               <- Stream.emit(gameInfoUpdated.state.current).through(turns.publish)
            _               <- gameVar.put(gameInfoUpdated).toStream
          } yield ()
    }
}