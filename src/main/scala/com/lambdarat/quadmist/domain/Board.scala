package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.Board.{Grid, Hand}
import com.lambdarat.quadmist.domain.Common.Color

import enumeratum._

/**
  * Possible states of a square.
  */
sealed trait Square extends EnumEntry

object Square extends Enum[Square] {

  val values = findValues

  case object Block extends Square { override def toString = "B" }

  case object Free extends Square { override def toString = "F" }

  case class Occupied(card: Card, color: Color) extends Square {
    override def toString = s"${card.id},${color}"
  }

}

case class BoardSize(value: Int)      extends AnyVal
case class BoardMaxBlocks(value: Int) extends AnyVal

case class BoardSettings(size: BoardSize, maxBlocks: BoardMaxBlocks)

final case class Board(
    grid: Grid,
    redHand: Hand,
    blueHand: Hand,
    settings: BoardSettings
) {
  override def toString = grid.map(row => row.mkString(" ")).mkString("\n")
}

case class XAxis(value: Int) extends AnyVal
case class YAxis(value: Int) extends AnyVal

case class Coordinates(x: XAxis, y: YAxis)

object Board {
  type Hand = Set[Card]
  type Grid = Array[Array[Square]]

  implicit class SafeGridAccess(grid: Grid) {

    def coords(xAxis: XAxis)(yAxis: YAxis): Square = grid(xAxis.value)(yAxis.value)

    def update(xAxis: XAxis)(yAxis: YAxis)(value: Square): Unit =
      grid(xAxis.value)(yAxis.value) = value

  }
}
