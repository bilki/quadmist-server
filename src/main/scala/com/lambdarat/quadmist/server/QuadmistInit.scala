package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.repository.GameRepository

import memeid4s.literal._

import cats.effect.Sync
import cats.implicits._

trait QuadmistInit[F[_]] {
  def init: F[Unit]
}

object QuadmistInit {

  def apply[F[_]](implicit instance: QuadmistInit[F]): QuadmistInit[F] = instance

  implicit def quadmistInit[F[_]: Sync](implicit gr: GameRepository[F]): QuadmistInit[F] =
    new QuadmistInit[F] {
      override def init: F[Unit] = {
        val players = List(
          Player.Id(uuid"7919293f-88b9-411e-9920-57bff4c5a8cf") -> Player.Name("Roberto"),
          Player.Id(uuid"7d588a91-c7a1-4a02-8f78-c3929c119849") -> Player.Name("Sergio")
        )

        players.traverse { case (id, name) => GameRepository[F].newPlayer(id, name) }.void
      }
    }

}
