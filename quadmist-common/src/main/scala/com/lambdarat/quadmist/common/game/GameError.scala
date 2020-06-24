package com.lambdarat.quadmist.common.game

import com.lambdarat.quadmist.common.domain.{Card, CardClass, Player}

import memeid4s.cats.implicits._

import cats.Show
import cats.data.NonEmptyList
import cats.implicits._

sealed trait GameError extends Throwable {
  def msg: String
}

object GameError {
  final case class InvalidPlayer(invalid: Player.Id)                     extends GameError {
    override val msg: String = s"Player [${invalid.toUUID.show}] not found"
  }
  final case class InvalidCard(invalid: Card.Id)                         extends GameError {
    override val msg: String = s"Card [${invalid.toUUID.show}] not found"
  }
  final case class NotOwnedCard(id: Card.Id, player: Player.Id)          extends GameError {
    override def msg: String =
      s"Card [${id.toUUID.show}] is not owned by player [${player.toUUID.show}]"
  }
  final case class InvalidCardClass(invalid: CardClass.Id)               extends GameError {
    override val msg: String = s"CardClass [${invalid.toUUID.show}] not found"
  }
  final case object PlayerSlotsFull                                      extends GameError {
    override val msg: String = "Player slots already filled"
  }
  final case class PlayerAlreadyJoined(id: Player.Id)                    extends GameError {
    override def msg: String = s"Player [${id.toUUID.show}] already joined the game"
  }
  final case class PlayerNeverJoined(id: Player.Id)                      extends GameError {
    override def msg: String = s"Player [${id.toUUID.show}] never joined the game"
  }
  final case class InvalidTransition(event: GameEvent, phase: GamePhase) extends GameError {
    override val msg: String =
      s"Invalid transition for event:phase [${event.entryName}:${phase.enumEntry}]"
  }
  final case class InvalidEvent(error: String)                           extends GameError {
    override def msg: String = s"Invalid event [$error]"
  }
  final case class MultipleErrors(errors: NonEmptyList[GameError])       extends GameError {
    override def msg: String = s"Multiple errors: [${errors.map(_.msg).mkString_(",\n")}]"
  }

  implicit val showGameError: Show[GameError] = Show(_.msg)
}
