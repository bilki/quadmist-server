package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.common.game.TurnState

/** Contains the full historic and current turns of game.
  *
  * @param current current board and turn state
  * @param history historic board and turn already played
  */
final case class GameState(
    current: TurnState,
    history: List[TurnState]
)
