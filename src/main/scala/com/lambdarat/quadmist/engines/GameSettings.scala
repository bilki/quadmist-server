package com.lambdarat.quadmist.engines

/**
  * Establish settings for game.
  *
  * @param cardMaxLevel maximum level for card stats
  * @param maxHandCards maximum number for cards in hand
  */
case class GameSettings(cardMaxLevel: Int, maxHandCards: Int)

object GameSettings {
  final val default = GameSettings(cardMaxLevel = 16, maxHandCards = 5)
}
