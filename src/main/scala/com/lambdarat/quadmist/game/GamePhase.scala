package com.lambdarat.quadmist.game

import enumeratum.{Enum, EnumEntry}

sealed trait GamePhase extends EnumEntry

object GamePhase extends Enum[GamePhase] {
  val values = findValues

  case object Initial       extends GamePhase
  case object PlayerOneTurn extends GamePhase
  case object PlayerTwoTurn extends GamePhase
  case object Finish        extends GamePhase
}
