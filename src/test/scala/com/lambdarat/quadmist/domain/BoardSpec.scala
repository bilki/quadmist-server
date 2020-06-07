package com.lambdarat.quadmist.domain

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
          board.grid.length shouldBe boardSettings.size.value
        }
      }

      "have a number of blocks less or equal than max blocks" in {
        forAll { board: Board =>
          lazy val numBlocks: Int = board.grid.foldLeft(0)(sumTotalRows)

          def sumTotalRows(total: Int, row: Array[Square]): Int = {
            lazy val rowBlocks = row.foldLeft(0)(sumRow)
            total + rowBlocks
          }

          def sumRow(blocks: Int, cell: Square): Int = cell match {
            case Block => blocks + 1
            case _     => blocks
          }

          numBlocks should be <= boardSettings.maxBlocks.value
        }
      }
    }

  }

}
