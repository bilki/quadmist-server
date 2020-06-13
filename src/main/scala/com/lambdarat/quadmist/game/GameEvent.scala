package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.{Card, Coordinates, Player}

import enumeratum.{Enum, EnumEntry}

sealed trait GameEvent extends EnumEntry

object GameEvent extends Enum[GameEvent] {
  val values = findValues

  case class PlayerJoined(player: Player)                extends GameEvent
  case class PlayerTurn(coords: Coordinates, card: Card) extends GameEvent
  case object TurnTimeout                                extends GameEvent
}
