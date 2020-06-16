package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.game.codecs._
import com.lambdarat.quadmist.game.{GameInfo, TurnState}
import com.lambdarat.quadmist.repository.GameRepository

import fs2.concurrent.Topic
import io.circe.syntax._
import memeid4s.UUID
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame.Text

import cats.effect._
import cats.effect.concurrent.MVar
import cats.implicits._
import cats.instances.unit

object QuadmistRoutes {
  def service[F[_]: ConcurrentEffect: Timer: GameRepository](
      gameInfoVar: MVar[F, GameInfo],
      gameTopic: Topic[F, TurnState]
  ): HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / UUID(id) =>
        val playerId = Player.Id(id)

        val gameSubscription = gameTopic
          .subscribe(10)
          .map(turn => Text(turn.asJson.noSpaces))

        val initPlayer = for {
          _              <- GameRepository[F].getPlayer(playerId)
          gameInfo       <- gameInfoVar.take
          gameInfoUpdated = gameInfo.playerOne
                              .fold(gameInfo.copy(playerOne = playerId.some))(_ =>
                                gameInfo.copy(playerTwo = playerId.some)
                              )
          ws             <- WebSocketBuilder[F].build(gameSubscription, _.as(unit))
          _              <- gameInfoVar.put(gameInfoUpdated)
        } yield ws

        initPlayer.handleErrorWith(err => BadRequest(err.getMessage))
    }
  }
}
