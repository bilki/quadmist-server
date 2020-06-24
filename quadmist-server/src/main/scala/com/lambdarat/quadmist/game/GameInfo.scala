package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.common.domain.Player
import com.lambdarat.quadmist.common.game.GamePhase

/**
  * Global game information.
  *
  * @param state current turn and historic
  * @param phase current phase of game
  * @param playerOne completed with player id when one player joins (RED)
  * @param playerTwo completed with player id when one player joins (BLUE)
  */
final case class GameInfo(
    state: GameState,
    phase: GamePhase,
    playerOne: Option[Player.Id],
    playerTwo: Option[Player.Id]
)
