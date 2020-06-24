package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.common.domain.{Card, Player, Settings}
import com.lambdarat.quadmist.common.game.GamePhase.Initial
import com.lambdarat.quadmist.common.game.{GameError, TurnFights, TurnState}
import com.lambdarat.quadmist.common.utils.{BoardGenerator, Identified}
import com.lambdarat.quadmist.game._
import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.QuadmistCommon._

import fs2.Stream
import fs2.concurrent.Topic
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

import java.time.Instant

import cats.effect.concurrent.MVar
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import cats.implicits._

object QuadmistServer {
  def server[F[_]: ConcurrentEffect: QuadmistInit: GameRepository: GameStateMachine](implicit
      T: Timer[F],
      C: ContextShift[F]
  ): Stream[F, ExitCode] = {

    val randomBoard = BoardGenerator.random(
      redPlayer = Set.empty[Card],
      bluePlayer = Set.empty[Card],
      gameSettings = Settings.default
    )

    val initialState = TurnState(
      randomBoard,
      TurnFights(List.empty, Instant.now)
    )

    val initialGameInfo = GameInfo(
      state = GameState(initialState, List.empty[TurnState]),
      phase = Initial,
      playerOne = none[Player.Id],
      playerTwo = none[Player.Id]
    )

    for {
      _                <- QuadmistInit[F].init.toStream
      gameTopic        <- Topic(initialState.asRight[Identified[Player.Id, GameError]]).toStream
      gameInfo         <- MVar[F].of(initialGameInfo).toStream
      httpApp           = QuadmistRoutes.service(gameInfo, gameTopic).orNotFound
      httpAppWithLogger = Logger.httpApp(true, true)(httpApp)
      exitCode         <- BlazeServerBuilder[F](global)
                            .bindHttp(8080, "0.0.0.0")
                            .withHttpApp(httpAppWithLogger)
                            .serve
    } yield exitCode
  }

}
