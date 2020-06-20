package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.{Card, CardClass, Player}

import memeid4s.cats.implicits._

import cats.data.NonEmptyList
import cats.implicits._

sealed trait GameError extends Throwable {
  def msg: String
}

object GameError {
  case class InvalidPlayer(invalid: Player.Id)                   extends GameError {
    override val msg: String = s"Player [${invalid.toUUID.show}] not found"
  }
  case class InvalidCard(invalid: Card.Id)                       extends GameError {
    override val msg: String = s"Card [${invalid.toUUID.show}] not found"
  }
  case class NotOwnedCard(id: Card.Id, player: Player.Id)        extends GameError {
    override def msg: String =
      s"Card [${id.toUUID.show}] is not owned by player [${player.toUUID.show}]"
  }
  case class InvalidCardClass(invalid: CardClass.Id)             extends GameError {
    override val msg: String = s"CardClass [${invalid.toUUID.show}] not found"
  }
  case object PlayerSlotsFull                                    extends GameError {
    override val msg: String = "Player slots already filled"
  }
  case class PlayerAlreadyJoined(id: Player.Id)                  extends GameError {
    override def msg: String = s"Player [${id.toUUID.show}] already joined the game"
  }
  case class InvalidTransition(info: GameInfo, event: GameEvent) extends GameError {
    override val msg: String = s"Invalid transition for event [${event.entryName}]"
  }
  case class InvalidEvent(error: String)                         extends GameError {
    override def msg: String = s"Invalid event [$error]"
  }
  case class MultipleErrors(errors: NonEmptyList[GameError])     extends GameError {
    override def msg: String = s"Multiple errors: [${errors.map(_.msg).mkString_(",\n")}]"
  }
}
