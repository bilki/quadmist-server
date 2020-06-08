package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.Board.{BoardMaxBlocks, BoardSize, Grid, Hand, XAxis, YAxis}
import com.lambdarat.quadmist.domain.Common.Color

import enumeratum._
import io.estatico.newtype.macros.newtype

import cats.Show

/**
  * Possible states of a square.
  */
sealed trait Square extends EnumEntry

object Square extends Enum[Square] {

  val values = findValues

  case object Block extends Square { override def toString = "B" }

  case object Free extends Square { override def toString = "F" }

  case class Occupied(card: Card, color: Color) extends Square {
    override def toString = s"${card.cardClassId},${color}"
  }

}

case class BoardSettings(size: BoardSize, maxBlocks: BoardMaxBlocks)

final case class Board(
    grid: Grid,
    redHand: Hand,
    blueHand: Hand,
    settings: BoardSettings
)

case class Coordinates(x: XAxis, y: YAxis)

object Board {
  @newtype case class BoardSize(toInt: Int)
  @newtype case class BoardMaxBlocks(toInt: Int)

  @newtype case class XAxis(toInt: Int)
  @newtype case class YAxis(toInt: Int)

  @newtype case class Hand(cards: Set[Card])
  @newtype case class Grid(squares: Array[Array[Square]])

  implicit val showBoard: Show[Board] =
    Show[Board](_.grid.squares.map(row => row.mkString(" ")).mkString(System.lineSeparator))

  implicit class GridAccess(grid: Grid) {
    def getSquare(x: XAxis)(y: YAxis): Square               = grid.squares(x.toInt)(y.toInt)
    def setSquare(x: XAxis)(y: YAxis)(square: Square): Unit =
      grid.squares(x.toInt)(y.toInt) = square
  }
}
