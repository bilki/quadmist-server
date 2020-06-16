package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.{Card, Player}

sealed trait GameError extends Throwable

object GameError {
  case class InvalidPlayer(invalid: Player.Id)                   extends GameError
  case class InvalidHand(invalid: List[Card.Id])                 extends GameError
  case class InvalidTransition(info: GameInfo, event: GameEvent) extends GameError
}
