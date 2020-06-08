package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Snapshot of a match.
  *
  * @param board distribution of cards and blocks
  * @param fights list of already computed fights
  */
final case class MatchSnapshot(
    board: Board,
    fights: List[Fight]
)

object MatchSnapshot {
  @newtype case class Id(toUUID: UUID)
}
