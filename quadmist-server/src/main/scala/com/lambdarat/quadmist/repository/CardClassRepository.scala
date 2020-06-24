package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.common.domain.CardClass
import com.lambdarat.quadmist.common.game.GameError.InvalidCardClass
import com.lambdarat.quadmist.repository.dto.CardClassDTO

import memeid4s.UUID
import memeid4s.cats.implicits._

import scala.collection.concurrent.TrieMap

import cats.effect.IO

trait CardClassRepository[F[_]] {
  def getCardClass(id: CardClass.Id): F[CardClassDTO]
  def createCardClass(name: CardClass.Name): F[CardClass.Id]
  def saveCardClass(cardClass: CardClassDTO): F[Boolean]
}

object CardClassRepository {

  implicit val memRepo = new CardClassRepository[IO] {
    val cardClasses: TrieMap[CardClass.Id, CardClassDTO] = TrieMap.empty

    override def getCardClass(id: CardClass.Id): IO[CardClassDTO] =
      IO.fromOption(cardClasses.get(id))(InvalidCardClass(id))

    override def createCardClass(name: CardClass.Name): IO[CardClass.Id] =
      for {
        cardClassId <- UUID.random[IO].map(CardClass.Id.apply)
        _           <- IO(cardClasses.put(cardClassId, CardClassDTO(cardClassId, CardClass(name))))
      } yield cardClassId

    override def saveCardClass(cardClass: CardClassDTO): IO[Boolean] =
      IO(cardClasses.putIfAbsent(cardClass.id, cardClass).isEmpty)
  }
}
