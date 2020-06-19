package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.game.GameError.PlayerSlotsFull
import com.lambdarat.quadmist.game.GameEvent.{PlayerJoined, TurnTimeout}
import com.lambdarat.quadmist.game.codecs._
import com.lambdarat.quadmist.game.{GameError, GameEvent, GameInfo, GameStateMachine, TurnState}
import com.lambdarat.quadmist.repository.GameRepository

import fs2.Pipe
import fs2.concurrent.Topic
import io.circe.syntax._
import memeid4s.UUID
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame.{Close, Text}

import cats.effect._
import cats.effect.concurrent.MVar
import cats.implicits._
import cats.instances.unit
import fs2.Stream
import org.http4s.websocket.WebSocketFrame
import io.circe.parser.decode

object QuadmistRoutes {
  def service[F[_]: ConcurrentEffect: Timer: GameRepository: GameStateMachine](
      gameInfoVar: MVar[F, GameInfo],
      gameTopic: Topic[F, TurnState]
  ): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "join" / UUID(id) =>
        val playerId = Player.Id(id)

        val gameSubscription = gameTopic
          .subscribe(10)
          .map(turn => Text(turn.asJson.noSpaces))

        val gameStateMachine = GameStateMachine[F].transition(gameInfoVar, gameTopic)

        val decodeEvents: Pipe[F, WebSocketFrame, GameEvent] = _.collect {
          case Text(jsonEvt, _) => decode[GameEvent](jsonEvt)
        }.rethrow

        val turnGenerator = decodeEvents.andThen(gameStateMachine)

        val initPlayer = for {
          _  <- GameRepository[F].getPlayer(playerId)
          _  <- Stream
                  .emit[F, GameEvent](PlayerJoined(playerId))
                  .through(gameStateMachine)
                  .compile
                  .drain
          ws <- WebSocketBuilder[F].build(gameSubscription, turnGenerator)
        } yield ws

        initPlayer.handleErrorWith {
          case err: GameError =>
            WebSocketBuilder[F]
              .build(Stream.emit(Text(err.msg)) ++ Stream.fromEither[F](Close(1002)), _.as(unit))
        }
    }
  }
}
