package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Snapshot of a match.
  *
  * @param board distribution of cards and blocks
  * @param fights list of already computed fights
  */
final case class MatchSnapshot private (
    board: Board,
    fights: List[Fight],
    id: MatchSnapshot.Id
) {
  private def copy(): Unit = {}
}

object MatchSnapshot {
  @newtype case class Id(toUUID: UUID)

  def apply(board: Board, fights: List[Fight]): MatchSnapshot = {
    val uniqueId = Id(UUID.V4.random)
    MatchSnapshot(board, fights, uniqueId)
  }
}
