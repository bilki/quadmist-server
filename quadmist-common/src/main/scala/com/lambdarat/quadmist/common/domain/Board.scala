package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Board.{Grid, Hand}
import com.lambdarat.quadmist.common.domain.Coordinates.{XAxis, YAxis}

import cats.Show

final case class Board(
    grid: Grid,
    redHand: Hand,
    blueHand: Hand,
    settings: Settings
)

object Board {
  type Grid = Array[Array[Square]]
  type Hand = Set[Card]

  implicit val showBoard: Show[Board] =
    Show(_.grid.map(row => row.mkString("|")).mkString(System.lineSeparator))

  implicit class GridAccess(grid: Grid) {
    def getSquare(x: XAxis)(y: YAxis): Square               = grid(x.toInt)(y.toInt)
    def setSquare(x: XAxis)(y: YAxis)(square: Square): Unit = grid(x.toInt)(y.toInt) = square
  }
}
