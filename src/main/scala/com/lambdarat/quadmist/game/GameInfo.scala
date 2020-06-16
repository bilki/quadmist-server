package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Player

case class GameInfo(
    state: GameState,
    phase: GamePhase,
    playerOne: Option[Player.Id],
    playerTwo: Option[Player.Id]
)
