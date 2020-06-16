package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Board.{BoardMaxBlocks, BoardSize}
import com.lambdarat.quadmist.domain._
import com.lambdarat.quadmist.engines._
import com.lambdarat.quadmist.game.GamePhase.Initial
import com.lambdarat.quadmist.game.{GameInfo, GameState, GameTurn, TurnState}
import com.lambdarat.quadmist.repository.GameRepository

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

  val defaultSettings: BoardSettings = BoardSettings(
    BoardSize(4),
    BoardMaxBlocks(6)
  )

  def server[F[_]: ConcurrentEffect: QuadmistInit: GameRepository](implicit
      T: Timer[F],
      C: ContextShift[F]
  ): Stream[F, ExitCode] = {

    val randomBoard = board.random(
      redPlayer = Set.empty[Card],
      bluePlayer = Set.empty[Card],
      boardSettings = defaultSettings
    )

    val initialState = TurnState(
      randomBoard,
      GameTurn(List.empty, Instant.now)
    )

    val initialGameInfo = GameInfo(
      state = GameState(initialState, List.empty[TurnState]),
      phase = Initial,
      playerOne = none[Player.Id],
      playerTwo = none[Player.Id]
    )

    for {
      _                <- Stream.eval(QuadmistInit[F].init)
      gameTopic        <- Stream.eval(Topic[F, TurnState](initialState))
      gameInfo         <- Stream.eval(MVar[F].of(initialGameInfo))
      httpApp           = QuadmistRoutes.service(gameInfo, gameTopic).orNotFound
      httpAppWithLogger = Logger.httpApp(true, true)(httpApp)
      exitCode         <- BlazeServerBuilder[F](global)
                            .bindHttp(8080, "0.0.0.0")
                            .withHttpApp(httpAppWithLogger)
                            .serve
    } yield exitCode
  }

}
