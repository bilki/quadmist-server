package com.lambdarat.quadmist.domain

import enumeratum.{Enum, EnumEntry}

sealed trait PlayerAction extends EnumEntry

object PlayerAction extends Enum[PlayerAction] {
  val values = findValues

  case object Join extends PlayerAction

  case object Leave extends PlayerAction

  case object Ready extends PlayerAction

  case object Start extends PlayerAction
}
