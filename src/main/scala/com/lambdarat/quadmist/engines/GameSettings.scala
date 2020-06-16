package com.lambdarat.quadmist.engines

class GameSettings(
    /**
      * Maximum level for card stats.
      */
    val CARD_MAX_LEVEL: Int,
    /**
      * Maximum number for cards in hand.
      */
    val MAX_HAND_CARDS: Int
)

object GameSettings {
  final val default = new GameSettings(
    CARD_MAX_LEVEL = 16,
    MAX_HAND_CARDS = 5
  )
}
