package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Common.Color
import com.lambdarat.quadmist.domain.{Arrow, Card, Coordinates, Player}

import enumeratum.{CirceEnum, Enum, EnumEntry}

case class GameMovement(color: Color, coords: Coordinates, cardId: Card.Id, target: Option[Arrow])

sealed trait GameEvent extends EnumEntry

object GameEvent extends Enum[GameEvent] with CirceEnum[GameEvent] {
  val values = findValues

  case object PlayerJoined                        extends GameEvent
  case class PlayerHand(initialHand: InitialHand) extends GameEvent
  case class PlayerMove(move: GameMovement)       extends GameEvent
  case class TurnTimeout(id: Player.Id)           extends GameEvent
  case object GameFinished                        extends GameEvent
}
