package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.common.domain.{Card, CardClass, Player}

object dto {
  final case class CardDTO(id: Card.Id, cclass: CardClass.Id, owner: Player.Id, card: Card)
  final case class CardClassDTO(id: CardClass.Id, cardClass: CardClass)
  final case class PlayerDTO(id: Player.Id, player: Player)
}
