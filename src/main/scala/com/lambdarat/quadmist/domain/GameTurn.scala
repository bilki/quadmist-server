package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid.UUID

/**
  * Actions that happened during one game turn.
  *
  * @param fights fights for this turn
  */
case class GameTurn(
    fights: List[Fight]
)

object GameTurn {
  @newtype case class Id(toUUID: UUID)
}
