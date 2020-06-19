package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.game.GameEvent.PlayerJoined
import com.lambdarat.quadmist.game.codecs._
import com.lambdarat.quadmist.game.{GameError, GameEvent, GameStateMachine}
import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.QuadmistCommon._

import fs2.{Pipe, Stream}
import io.circe.parser.decode
import io.circe.syntax._
import memeid4s.UUID
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.{Close, Text}

import cats.effect._
import cats.implicits._
import cats.instances.unit

object QuadmistRoutes {
  def service[F[_]: ConcurrentEffect: Timer: GameRepository: GameStateMachine](
      gameInfoVar: Game[F],
      gameTopic: Turns[F]
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
