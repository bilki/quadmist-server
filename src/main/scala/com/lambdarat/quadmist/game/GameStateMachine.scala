package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Common.Color.{Blue, Red}
import com.lambdarat.quadmist.game
import com.lambdarat.quadmist.game.GameError.{InvalidTransition, PlayerSlotsFull}
import com.lambdarat.quadmist.game.GameEvent.{
  GameFinished,
  PlayerJoined,
  PlayerMove,
  PlayerRequestHand
}
import com.lambdarat.quadmist.game.GamePhase.{Finish, Initial, PlayerBlueTurn, PlayerRedTurn}
import com.lambdarat.quadmist.repository.GameRepository

import fs2.concurrent.Topic
import fs2.{Pipe, Stream}

import cats.effect.Sync
import cats.effect.concurrent.MVar
import cats.implicits._

trait GameStateMachine[F[_]] {
  def transition(
      gameInfoVar: MVar[F, GameInfo],
      topic: Topic[F, TurnState]
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
    (gameInfoVar: MVar[F, GameInfo], topic: Topic[F, TurnState]) =>
      gameEvents =>
        for {
          gameInfo        <- Stream.eval(gameInfoVar.take)
          event           <- gameEvents
          f                = Sync[F]
          gameInfoUpdated <- Stream.eval(chooseNextTransition(gameInfo, event).handleErrorWith {
                               case gameError: GameError =>
                                 for {
                                   _ <- gameInfoVar.put(gameInfo)
                                   _ <- f.raiseError[Unit](gameError) // TODO send to topic...
                                 } yield gameInfo
                             })
          _               <- Stream.emit(gameInfoUpdated.state.current).through(topic.publish)
          _               <- Stream.eval(gameInfoVar.put(gameInfoUpdated))
        } yield ()
}
