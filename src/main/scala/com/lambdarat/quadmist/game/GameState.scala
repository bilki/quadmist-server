package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Board

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Turn description.
  *
  * @param board state of the board
  * @param turn extra information about turn
  */
final case class TurnState(
    board: Board,
    turn: GameTurn
)

/**
  * Snapshot of a match.
  *
  * @param current current board and turn state
  * @param history historic board and turn already played
  */
final case class GameState(
    current: TurnState,
    history: List[TurnState]
)
