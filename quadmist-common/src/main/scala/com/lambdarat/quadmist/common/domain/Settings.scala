package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Settings.{
  BoardMaxBlocks,
  BoardSize,
  CardMaxLevel,
  MaxHandCards
}

import io.estatico.newtype.macros.newtype

/**
  * Game settings range from board size to card stats.
  *
  * @param boardSideSize size of board side
  * @param boardMaxBlocks maximum number of board blocks
  * @param cardMaxLevel cardMaxLevel maximum level for card stats
  * @param maxHandCards maxHandCards maximum number of cards in hand
  */
final case class Settings(
    boardSideSize: BoardSize,
    boardMaxBlocks: BoardMaxBlocks,
    cardMaxLevel: CardMaxLevel,
    maxHandCards: MaxHandCards
)

object Settings {
  @newtype case class BoardSize(toInt: Int)
  @newtype case class BoardMaxBlocks(toInt: Int)
  @newtype case class CardMaxLevel(toInt: Int)
  @newtype case class MaxHandCards(toInt: Int)

  val default: Settings = Settings(
    BoardSize(4),
    BoardMaxBlocks(6),
    CardMaxLevel(16),
    MaxHandCards(5)
  )
}
