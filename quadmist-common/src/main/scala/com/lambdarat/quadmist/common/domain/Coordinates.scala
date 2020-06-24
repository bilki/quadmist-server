package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Coordinates.{XAxis, YAxis}

import io.estatico.newtype.macros.newtype

/**
  * 2D coordinates
  * @param x horizontal axis
  * @param y vertical axis
  */
final case class Coordinates(x: XAxis, y: YAxis)

object Coordinates {
  @newtype case class XAxis(toInt: Int)
  @newtype case class YAxis(toInt: Int)
}
