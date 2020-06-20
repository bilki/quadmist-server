package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.{Card, CardClass, Player}

import memeid4s.cats.implicits._

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
  case class InvalidCardClass(invalid: CardClass.Id)             extends GameError {
    override val msg: String = s"CardClass [${invalid.toUUID.show}] not found"
  }
  case object PlayerSlotsFull                                    extends GameError {
    override val msg: String = "Player slots already filled"
  }
  case class InvalidHand(invalid: List[Card.Id])                 extends GameError {
    override val msg: String = s"Hand [${invalid.map(_.toUUID.show).show}] contains invalid cards"
  }
  case class InvalidTransition(info: GameInfo, event: GameEvent) extends GameError {
    override val msg: String = s"Invalid transition for event [${event.entryName}]"
  }
  case class InvalidEvent(error: String)                         extends GameError {
    override def msg: String = s"Invalid event [$error]"
  }
}
