package com.lambdarat.quadmist.common.game

import com.lambdarat.quadmist.common.domain.Board

/**
  * Turn description.
  *
  * @param board state of the board
  * @param turn extra information about turn
  */
final case class TurnState(
    board: Board,
    turn: TurnFights
)
