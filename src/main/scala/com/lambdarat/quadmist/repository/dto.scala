package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.domain.{Card, CardClass, Player}

object dto {
  case class CardDTO(id: Card.Id, cclass: CardClass.Id, owner: Player.Id, card: Card)
  case class CardClassDTO(id: CardClass.Id, cardClass: CardClass)
  case class PlayerDTO(id: Player.Id, player: Player)
}
