package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Common.Color
import com.lambdarat.quadmist.domain.{Arrow, Card, Coordinates, Player}

import enumeratum.{CirceEnum, Enum, EnumEntry}

case class GameMovement(color: Color, coords: Coordinates, cardId: Card.Id, target: Option[Arrow])

sealed trait GameEvent extends EnumEntry

object GameEvent extends Enum[GameEvent] with CirceEnum[GameEvent] {
  val values = findValues

  case class PlayerJoined(id: Player.Id)                                extends GameEvent
  case class PlayerRequestHand(id: Player.Id, initialHand: InitialHand) extends GameEvent
  case class PlayerMove(id: Player.Id, move: GameMovement)              extends GameEvent
  case class TurnTimeout(id: Player.Id)                                 extends GameEvent
  case object GameFinished                                              extends GameEvent
}
