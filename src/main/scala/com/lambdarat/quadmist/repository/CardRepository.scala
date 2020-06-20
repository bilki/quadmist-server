package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.domain.{Card, CardClass, Player}
import com.lambdarat.quadmist.game.GameError.InvalidCard
import com.lambdarat.quadmist.repository.dto.CardDTO

import memeid4s.UUID
import memeid4s.cats.implicits._

import scala.collection.concurrent.TrieMap

import cats.effect.IO

trait CardRepository[F[_]] {
  def getCard(id: Card.Id): F[CardDTO]
  def generateCard(cclass: CardClass.Id, owner: Player.Id, card: Card): F[Card.Id]
  def newCard(id: Card.Id, cclass: CardClass.Id, owner: Player.Id, card: Card): F[Boolean]
  def getCardsBy(id: Player.Id): F[List[CardDTO]]
}

object CardRepository {
  def apply[F[_]](implicit cr: CardRepository[F]): CardRepository[F] = cr

  implicit val memRepo = new CardRepository[IO] {
    val cards: TrieMap[Card.Id, CardDTO] = TrieMap.empty

    override def getCard(id: Card.Id): IO[CardDTO] =
      IO.fromOption(cards.get(id))(InvalidCard(id))

    override def generateCard(cclass: CardClass.Id, owner: Player.Id, card: Card): IO[Card.Id] =
      for {
        cardId <- UUID.random[IO].map(Card.Id.apply)
        _      <- IO(cards.put(cardId, CardDTO(cardId, cclass, owner, card)))
      } yield cardId

    override def newCard(
        id: Card.Id,
        cclass: CardClass.Id,
        owner: Player.Id,
        card: Card
    ): IO[Boolean] = IO(cards.putIfAbsent(id, CardDTO(id, cclass, owner, card)).isEmpty)

    override def getCardsBy(id: Player.Id): IO[List[CardDTO]] =
      IO(cards.filter { case (_, CardDTO(_, _, ownerId, _)) => ownerId == id }.values.toList)
  }
}
