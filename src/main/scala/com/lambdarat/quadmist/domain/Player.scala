package com.lambdarat.quadmist.domain

/**
  * A game player.
  *
  * @param id unique identifier
  * @param cards cards owned by the player
  */
final case class Player(id: Player.Id, cards: List[Card.Id])

object Player {

  case class Id(value: Int) extends AnyVal

}
