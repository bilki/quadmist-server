package com.lambdarat.quadmist.common.utils

import com.lambdarat.quadmist.common.domain.Board.{Grid, Hand}
import com.lambdarat.quadmist.common.domain.Square.{Block, Free}
import com.lambdarat.quadmist.common.domain.{Board, Settings}

import scala.util.Random

object BoardGenerator {

  /**
    * Creates a fresh free board with some random blocks on it and the red and
    * blue player hands of cards.
    */
  def random(redPlayer: Hand, bluePlayer: Hand, gameSettings: Settings): Board = {
    val randomBlocks: Int = Random.nextInt(gameSettings.boardMaxBlocks.toInt + 1)
    val boardSideSize     = gameSettings.boardSideSize.toInt

    // All possible coords of the grid
    val coords: Array[(Int, Int)] = Array((for {
      i <- 0 until boardSideSize
      j <- 0 until boardSideSize
    } yield (i, j)): _*)

    // Fisher-Yates
    for {
      i <- coords.length - 1 to 1 by -1
      j  = Random.nextInt(i + 1)
    } {
      val aux = coords(i)
      coords(i) = coords(j)
      coords(j) = aux
    }

    // Create a new grid of Free squares and then throw in the random blocks
    val squares: Grid = Array.fill(boardSideSize, boardSideSize)(Free)

    coords.take(randomBlocks).foreach {
      case (i, j) => squares(i)(j) = Block
    }

    Board(squares, redPlayer, bluePlayer, gameSettings)
  }
}
