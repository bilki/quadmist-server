package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.repository.GameRepository
import com.lambdarat.quadmist.server.InitSample._

import cats.effect.Sync
import cats.implicits._

trait QuadmistInit[F[_]] {
  def init: F[Unit]
}

object QuadmistInit {
  def apply[F[_]](implicit instance: QuadmistInit[F]): QuadmistInit[F] = instance

  implicit def quadmistInit[F[_]: Sync](implicit gr: GameRepository[F]): QuadmistInit[F] =
    new QuadmistInit[F] {
      override def init: F[Unit] =
        for {
          cardClasses <- loadCardClasses
          _           <- cardClasses.traverse { case CardClassEntry(id, name) =>
                           GameRepository[F].newCardClass(id, name)
                         }
          _           <- players.traverse { case (id, name) =>
                           GameRepository[F].newPlayer(id, name)
                         }
          _           <- cards.traverse { case (id, (playerId, cclasId, card)) =>
                           GameRepository[F].saveCardForPlayer(id, playerId, cclasId, card)
                         }
        } yield ()
    }

}
