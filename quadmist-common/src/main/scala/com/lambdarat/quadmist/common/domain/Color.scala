package com.lambdarat.quadmist.common.domain

import enumeratum.values.{StringCirceEnum, StringEnum, StringEnumEntry}

/**
  * Possible colors inside game.
  */
sealed abstract class Color(val value: String) extends StringEnumEntry { def flip: Color }

object Color extends StringEnum[Color] with StringCirceEnum[Color] {
  val values = findValues

  case object Red  extends Color("red")  { def flip: Color = Blue }
  case object Blue extends Color("blue") { def flip: Color = Red  }
}
