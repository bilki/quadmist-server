package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Snapshot of a match.
  *
  * @param board current board state
  * @param turn last played turn
  * @param boardHistory list of already played boards
  * @param turnHistory list of already played game turns
  */
final case class GameState(
    board: Board,
    turn: GameTurn,
    boardHistory: List[Board],
    turnHistory: List[GameTurn]
)

object GameState {
  @newtype case class Id(toUUID: UUID)
}
