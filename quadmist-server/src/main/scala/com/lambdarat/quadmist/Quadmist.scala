package com.lambdarat.quadmist

import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.QuadmistServer

import cats.effect.{ExitCode, IO, IOApp}

object Quadmist extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val gr = GameRepository[IO]

    QuadmistServer.server[IO].compile.lastOrError
  }
}
