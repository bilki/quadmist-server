package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.domain.{Card, CardClass, Player}
import com.lambdarat.quadmist.repository.dto.{CardClassDTO, PlayerDTO}
import com.lambdarat.quadmist.utils.Identified
import com.lambdarat.quadmist.utils.Identified._

import cats.effect.Sync
import cats.implicits._

trait GameRepository[F[_]] {
  def getPlayer(id: Player.Id): F[Player]
  def createPlayer(name: Player.Name): F[Player.Id]
  def newPlayer(id: Player.Id, name: Player.Name): F[Boolean]

  def getCards(ids: List[Card.Id]): F[List[Identified[Card.Id, Card]]]
  def getCard(id: Card.Id): F[Card]

  def createCardClass(name: CardClass.Name): F[CardClass.Id]
  def newCardClass(id: CardClass.Id, name: CardClass.Name): F[Boolean]

  def generateCardForPlayer(player: Player.Id, cclass: CardClass.Id, card: Card): F[Card.Id]
  def saveCardForPlayer(
      id: Card.Id,
      player: Player.Id,
      cclass: CardClass.Id,
      card: Card
  ): F[Boolean]
  def getCardsBy(player: Player.Id): F[List[Identified[Card.Id, Card]]]
}

object GameRepository {
  def apply[F[_]](implicit gr: GameRepository[F]): GameRepository[F] = gr

  implicit def gameRepository[F[_]: Sync](implicit
      playerRepo: PlayerRepository[F],
      cardRepo: CardRepository[F],
      cardClassRepo: CardClassRepository[F]
  ): GameRepository[F] =
    new GameRepository[F] {
      override def getPlayer(id: Player.Id): F[Player] = playerRepo.getPlayer(id).map(_.player)

      override def createPlayer(name: Player.Name): F[Player.Id] = playerRepo.createPlayer(name)

      override def newPlayer(id: Player.Id, name: Player.Name): F[Boolean] =
        playerRepo.savePlayer(PlayerDTO(id, Player(name)))

      override def getCards(ids: List[Card.Id]): F[List[Identified[Card.Id, Card]]] =
        ids.traverse(id => cardRepo.getCard(id).map(_.card.withId(id)))

      override def getCard(id: Card.Id): F[Card] = cardRepo.getCard(id).map(_.card)

      override def createCardClass(name: CardClass.Name): F[CardClass.Id] =
        cardClassRepo.createCardClass(name)

      override def newCardClass(id: CardClass.Id, name: CardClass.Name): F[Boolean] =
        cardClassRepo.saveCardClass(CardClassDTO(id, CardClass(name)))

      override def generateCardForPlayer(
          player: Player.Id,
          cclass: CardClass.Id,
          card: Card
      ): F[Card.Id] = cardRepo.generateCard(cclass, player, card)

      override def saveCardForPlayer(
          id: Card.Id,
          player: Player.Id,
          cclass: CardClass.Id,
          card: Card
      ): F[Boolean] = cardRepo.newCard(id, cclass, player, card)

      override def getCardsBy(player: Player.Id): F[List[Identified[Card.Id, Card]]] =
        cardRepo.getCardsBy(player).map(_.map(dto => Identified(dto.id, dto.card)))
    }
}
