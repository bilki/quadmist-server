package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * A game player.
  *
  * @param id unique identifier
  * @param cards cards owned by the player
  */
final case class Player(id: Player.Id, cards: List[Card.Id])

object Player {
  @newtype case class Id(toUUID: UUID)
}
