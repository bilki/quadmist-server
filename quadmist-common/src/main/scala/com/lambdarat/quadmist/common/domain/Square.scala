package com.lambdarat.quadmist.common.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}

/**
  * Possible states of a square.
  */
sealed trait Square extends EnumEntry

object Square extends Enum[Square] with CirceEnum[Square] {
  val values = findValues

  case object Block                                                   extends Square {
    override def toString = "B"
  }
  case object Free                                                    extends Square {
    override def toString = "F"
  }
  case class Occupied(cclass: CardClass.Id, card: Card, color: Color) extends Square {
    override def toString = s"${cclass.toUUID},$card,$color"
  }
}
