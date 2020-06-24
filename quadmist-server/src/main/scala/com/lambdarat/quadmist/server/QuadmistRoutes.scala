package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.common.codecs.codecs._
import com.lambdarat.quadmist.common.domain.Player
import com.lambdarat.quadmist.common.game.GameError.InvalidEvent
import com.lambdarat.quadmist.common.game.GameEvent.PlayerJoined
import com.lambdarat.quadmist.common.game.{GameError, GameEvent, TurnState}
import com.lambdarat.quadmist.common.utils.Identified._
import com.lambdarat.quadmist.game.GameStateMachine
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
      gameVar: Game[F],
      turns: Turns[F]
  ): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "join" / UUID(id) =>
        val playerId = Player.Id(id)

        // Listens to turns and also errors specifically sent to this player
        val turnsSubscription = turns
          .subscribe(10)
          .filter(_.fold(_.id == playerId, _ => true))
          .map(turn => Text(turn.leftMap(_.entity).asJson.noSpaces))

        val gameStateMachine = GameStateMachine[F].transition(playerId, gameVar, turns)

        val decodeEvents: Pipe[F, WebSocketFrame, Either[GameError, GameEvent]] = _.collect {
          case Text(jsonEvt, _) =>
            decode[GameEvent](jsonEvt).leftMap[GameError](err => InvalidEvent(err.show))
        }

        val turnGenerator = decodeEvents.andThen(_.flatMap {
          case Left(gameError) =>
            Stream.emit(gameError.withId(playerId).asLeft[TurnState]).through(turns.publish)
          case Right(event)    =>
            Stream.emit(event).through(gameStateMachine)
        })

        val initPlayer = for {
          _  <- GameRepository[F].getPlayer(playerId)
          _  <- Stream
                  .emit[F, GameEvent](PlayerJoined)
                  .through(gameStateMachine)
                  .compile
                  .drain
          ws <- WebSocketBuilder[F].build(turnsSubscription, turnGenerator)
        } yield ws

        initPlayer.handleErrorWith {
          case err: GameError =>
            WebSocketBuilder[F]
              .build(Stream.fromEither[F](Close(1002)).cons1(Text(err.asJson.noSpaces)), _.as(unit))
        }
    }
  }
}
