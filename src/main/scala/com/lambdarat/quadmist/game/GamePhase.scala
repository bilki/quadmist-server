package com.lambdarat.quadmist.game

import enumeratum.{Enum, EnumEntry}

sealed trait GamePhase extends EnumEntry

object GamePhase extends Enum[GamePhase] {
  val values = findValues

  case object Initial        extends GamePhase
  case object PlayerBlueTurn extends GamePhase
  case object PlayerRedTurn  extends GamePhase
  case object Finish         extends GamePhase
}
