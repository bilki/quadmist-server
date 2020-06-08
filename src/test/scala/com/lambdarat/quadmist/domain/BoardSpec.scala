package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.Board.{BoardMaxBlocks, BoardSize}
import com.lambdarat.quadmist.domain.ModelGens._
import com.lambdarat.quadmist.domain.Square.Block
import com.lambdarat.quadmist.engines.GameSettings

class BoardSpec extends ModelSpec {

  "A board" when {
    val BOARD_SIZE       = BoardSize(4)
    val BOARD_MAX_BLOCKS = BoardMaxBlocks(6)

    implicit val boardSettings: BoardSettings = BoardSettings(BOARD_SIZE, BOARD_MAX_BLOCKS)

    implicit val defaultGameSettings: GameSettings = new GameSettings {
      override val CARD_MAX_LEVEL: Int = 16
      override val MAX_HAND_CARDS: Int = 5
    }

    "created" should {
      "have the size specified" in {
        forAll { board: Board =>
          board.grid.squares.length shouldBe boardSettings.size.toInt
        }
      }

      "have a number of blocks less or equal than max blocks" in {
        forAll { board: Board =>
          val numBlocks: Int = board.grid.squares.flatten.collect { case Block => 1 }.sum

          numBlocks should be <= boardSettings.maxBlocks.toInt
        }
      }
    }

  }

}
