package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Fight

import io.estatico.newtype.macros.newtype
import memeid.UUID

import java.time.Instant

/**
  * Actions that happened during one game turn.
  *
  * @param fights fights for this turn
  * @param playedAt instant when game turn was played
  */
case class GameTurn(
    fights: List[Fight],
    playedAt: Instant
)

object GameTurn {
  @newtype case class Id(toUUID: UUID)
}
