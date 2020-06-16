package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Board.{BoardMaxBlocks, BoardSize, XAxis, YAxis}
import com.lambdarat.quadmist.domain.Card.{MagicalDef, PhysicalDef, Power}
import com.lambdarat.quadmist.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}
import com.lambdarat.quadmist.domain.Square.Occupied
import com.lambdarat.quadmist.domain._

import io.circe.Encoder
import io.circe.generic.semiauto._
import memeid4s.cats.implicits._

import cats.implicits._

object codecs {
  implicit val powerEncoder       = Encoder.encodeInt.contramap[Power](_.toInt)
  implicit val battleClassEncoder = Encoder.encodeString.contramap[BattleClass](_.entryName)
  implicit val physicalDefEncoder = Encoder.encodeInt.contramap[PhysicalDef](_.toInt)
  implicit val magicalDefEncoder  = Encoder.encodeInt.contramap[MagicalDef](_.toInt)
  implicit val arrowEncoder       = Encoder.encodeString.contramap[Arrow](_.entryName)
  implicit val cardEncoder        = deriveEncoder[Card]

  implicit val xaxisEncoder       = Encoder.encodeInt.contramap[XAxis](_.toInt)
  implicit val yaxisEncoder       = Encoder.encodeInt.contramap[YAxis](_.toInt)
  implicit val coordinatesEncoder = deriveEncoder[Coordinates]

  implicit val attackerEncoder       = deriveEncoder[Attacker]
  implicit val defenderEncoder       = deriveEncoder[Defender]
  implicit val attackerPointsencoder = Encoder.encodeInt.contramap[AttackerPoints](_.toInt)
  implicit val defenderPointsencoder = Encoder.encodeInt.contramap[DefenderPoints](_.toInt)
  implicit val attackerWinsEncoder   = Encoder.encodeBoolean.contramap[AttackerWins](_.toBool)
  implicit val fightEncoder          = deriveEncoder[Fight]

  implicit val boardSizeEncoder      = Encoder.encodeInt.contramap[BoardSize](_.toInt)
  implicit val boardMaxBlocksEncoder = Encoder.encodeInt.contramap[BoardMaxBlocks](_.toInt)
  implicit val boardSettingsEncoder  = deriveEncoder[BoardSettings]

  implicit val cardClassIdEncoder = Encoder.encodeString.contramap[CardClass.Id](_.toUUID.show)
  implicit val occupiedEncoder    = deriveEncoder[Occupied]
  implicit val squareEncoder      = deriveEncoder[Square]
  implicit val boardEncoder       = deriveEncoder[Board]

  implicit val gameTurnEncoder  = deriveEncoder[GameTurn]
  implicit val turnStateEncoder = deriveEncoder[TurnState]
}
