package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Player.Name

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * A game player.
  *
  *  @param name name of this player
  */
final case class Player(name: Name)

object Player {
  @newtype case class Id(toUUID: UUID)
  @newtype case class Name(toStr: String)
}
