package com.lambdarat.quadmist.engines

import com.lambdarat.quadmist.domain.Common.Color
import com.lambdarat.quadmist.domain.Common.Color.{Blue, Red}
import com.lambdarat.quadmist.domain.Square.{Block, Free, Occupied}
import com.lambdarat.quadmist.domain._

import scala.util.Random

trait BoardEngine {
  import Board._

  /**
    * Adds a new occupied square to the board.
    *
    * @param board     Board
    * @param newCoords new Coordinate
    * @param occupied  card and color
    * @return a new board with the occupied square
    */
  def add(board: Board, newCoords: Coordinates, occupied: Occupied, player: Color): Board = {
    // TODO replace these requires by Either or compile time checks
    // Target square position must be free
    require(areValidCoords(board, newCoords))
    require(board.grid.getSquare(newCoords.x)(newCoords.y) == Free)

    // Occupied card must be part of the hand of the selected player
    require(
      (board.redHand.contains(occupied.card) && player == Red) ||
        (board.blueHand.contains(occupied.card) && player == Blue)
    )

    board.grid.setSquare(newCoords.x)(newCoords.y)(occupied)

    val (newRed, newBlue) = player match {
      case Red  => (board.redHand - occupied.card, board.blueHand)
      case Blue => (board.redHand, board.blueHand - occupied.card)
    }

    val newGrid = board.grid.clone

    board.copy(grid = newGrid, redHand = newRed, blueHand = newBlue)
  }

  /**
    * Flips the card on the specified position.
    *
    * @param board  Board
    * @param coords Coordinate
    * @return a new board with the card flipped
    */
  def flip(board: Board, coords: Coordinates): Board = {
    // TODO replace these requires by Either or compile time checks
    require(areValidCoords(board, coords))
    require(board.grid.getSquare(coords.x)(coords.y).isInstanceOf[Occupied])

    board.grid.getSquare(coords.x)(coords.y) match {
      case Occupied(id, card, color) =>
        board.grid.setSquare(coords.x)(coords.y)(Occupied(id, card, color.flip))
      case Block | Free              => // ERROR
    }

    val newGrid = board.grid.clone

    board.copy(grid = newGrid)
  }

  /**
    * Flips all the cards on the specified positions.
    *
    * @param board  Board
    * @param coords Coordinate
    * @return a new board with the card flipped
    */
  def flipAll(board: Board, coords: List[Coordinates]): Board = {
    coords
      .filter(coords => areValidCoords(board, coords))
      .map(coords => flip(board, coords))

    val newGrid = board.grid.clone
    board.copy(grid = newGrid)
  }

  /**
    * Get the opponents for a card on the given coords.
    *
    * @param board  Board
    * @param coords Coordinate
    * @return a list of possible opponents and the direction of the attack
    */
  def opponents(board: Board, coords: Coordinates): List[(Card, Arrow)] = {
    // TODO replace these requires by Either or compile time checks
    require(areValidCoords(board, coords))
    require(board.grid.getSquare(coords.x)(coords.y).isInstanceOf[Occupied])

    board.grid.getSquare(coords.x)(coords.y) match {
      case Occupied(_, card, color) =>
        card.arrows
          .map(arrow => (arrow, Arrow.target(arrow, coords)))
          .filter { case (_, coords: Coordinates) => areValidCoords(board, coords) }
          .collect {
            case (arrow, arrowCoord) =>
              board.grid.getSquare(arrowCoord.x)(arrowCoord.y) match {
                case Occupied(_, enemyCard, enemyColor) if color != enemyColor =>
                  (enemyCard, arrow)
              }
          }

      case Block | Free             => List.empty // Maybe error
    }
  }

  /**
    * Retrieve all the cards from the board of the given color.
    *
    * @param board Board
    * @param color color of the cards to be retrieved
    * @return a list with all the cards on the board with that color
    */
  def cardsOf(board: Board, color: Color): List[Card] = {
    (board.grid flatMap { row =>
      row.collect {
        case Occupied(_, card, sqColor) if sqColor == color => card
      }
    }).toList

  }

  // Check against
  private def areValidCoords(board: Board, coords: Coordinates): Boolean = {
    coords.x.toInt >= 0 && coords.x.toInt < board.settings.size.toInt &&
    coords.y.toInt >= 0 && coords.y.toInt < board.settings.size.toInt
  }

  /**
    * Creates a fresh free board with some random blocks on it and the red and
    * blue player hands of cards.
    */
  def random(redPlayer: Hand, bluePlayer: Hand, boardSettings: BoardSettings): Board = {
    val randomBlocks: Int = Random.nextInt(boardSettings.maxBlocks.toInt + 1)

    // All possible coords of the grid
    val coords: Array[(Int, Int)] = Array((for {
      i <- 0 until boardSettings.size.toInt
      j <- 0 until boardSettings.size.toInt
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
    val squares: Grid =
      Array.fill(boardSettings.size.toInt, boardSettings.size.toInt)(Free)

    coords.take(randomBlocks).foreach {
      case (i, j) => squares(i)(j) = Block
    }

    Board(squares, redPlayer, bluePlayer, boardSettings)
  }

}
