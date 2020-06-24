package com.lambdarat.quadmist.common.game

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

sealed abstract class GamePhase(val value: String) extends StringEnumEntry

object GamePhase extends StringEnum[GamePhase] with StringCirceEnum[GamePhase] {
  val values = findValues

  case object Initial        extends GamePhase("initial")
  case object PlayerBlueTurn extends GamePhase("blueTurn")
  case object PlayerRedTurn  extends GamePhase("redTurn")
  case object Finish         extends GamePhase("finish")
}
