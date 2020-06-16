package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.domain
import com.lambdarat.quadmist.domain.CardClass
import com.lambdarat.quadmist.repository.dto.CardClassDTO

import memeid4s.UUID
import memeid4s.cats.implicits._

import scala.collection.concurrent.TrieMap

import cats.effect.IO

trait CardClassRepository[F[_]] {
  def getCardClass(id: CardClass.Id): F[CardClassDTO]
  def createCardClass(name: CardClass.Name): F[CardClass.Id]
}

object CardClassRepository {

  implicit val memRepo = new CardClassRepository[IO] {
    val cardClasses: TrieMap[domain.CardClass.Id, CardClassDTO] = TrieMap.empty

    override def getCardClass(id: CardClass.Id): IO[CardClassDTO] =
      IO.fromOption(cardClasses.get(id))(new Exception("CardClass not found"))

    override def createCardClass(name: CardClass.Name): IO[CardClass.Id] =
      for {
        cardClassId <- UUID.random[IO].map(CardClass.Id.apply)
        _           <- IO(cardClasses.put(cardClassId, CardClassDTO(cardClassId, CardClass(name))))
      } yield cardClassId
  }
}
