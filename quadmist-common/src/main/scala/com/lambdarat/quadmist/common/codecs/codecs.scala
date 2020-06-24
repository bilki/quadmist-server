package com.lambdarat.quadmist.common.codecs

import com.lambdarat.quadmist.common.domain.Card.{MagicalDef, PhysicalDef, Power}
import com.lambdarat.quadmist.common.domain.Coordinates.{XAxis, YAxis}
import com.lambdarat.quadmist.common.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}
import com.lambdarat.quadmist.common.domain.Settings.{
  BoardMaxBlocks,
  BoardSize,
  CardMaxLevel,
  MaxHandCards
}
import com.lambdarat.quadmist.common.domain.Square.Occupied
import com.lambdarat.quadmist.common.domain._
import com.lambdarat.quadmist.common.game.GameEvent.{PlayerHand, PlayerMove}
import com.lambdarat.quadmist.common.game._

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import memeid4s.cats.implicits._
import memeid4s.circe.implicits._

import cats.implicits._

object codecs {
  /* ---- ENCODERS ---- */
  implicit val powerEncoder       = Encoder.encodeInt.contramap[Power](_.toInt)
  implicit val battleClassEncoder = Encoder.encodeString.contramap[BattleClass](_.entryName)
  implicit val physicalDefEncoder = Encoder.encodeInt.contramap[PhysicalDef](_.toInt)
  implicit val magicalDefEncoder  = Encoder.encodeInt.contramap[MagicalDef](_.toInt)
  implicit val arrowEncoder       = deriveEncoder[Arrow]
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
  implicit val cardMaxLevelEncoder   = Encoder.encodeInt.contramap[CardMaxLevel](_.toInt)
  implicit val maxHandCardsEncoder   = Encoder.encodeInt.contramap[MaxHandCards](_.toInt)
  implicit val settingsEncoder       = deriveEncoder[Settings]

  implicit val cardClassIdEncoder = Encoder.encodeString.contramap[CardClass.Id](_.toUUID.show)
  implicit val occupiedEncoder    = deriveEncoder[Occupied]
  implicit val squareEncoder      = deriveEncoder[Square]
  implicit val boardEncoder       = deriveEncoder[Board]

  implicit val gameTurnEncoder     = deriveEncoder[TurnFights]
  implicit val turnStateEncoder    = deriveEncoder[TurnState]
  implicit val gameErrorEncoder    = Encoder.encodeString.contramap[GameError](_.msg)
  implicit val eventOutcomeEncoder = Encoder.encodeEither[GameError, TurnState]("error", "turn")

  /* ---- DECODERS ---- */
  implicit val cardClassIdDecoder   = UUIDDecoderInstance.map(CardClass.Id.apply)
  implicit val cardClassNameDecoder = Decoder.decodeString.map(CardClass.Name.apply)

  implicit val playerIdDecoder = UUIDDecoderInstance.map(Player.Id.apply)

  implicit val cardIdDecoder            = UUIDDecoderInstance.map(Card.Id.apply)
  implicit val initialHandDecoder       = deriveDecoder[InitialHand]
  implicit val playerRequestHandDecoder = deriveDecoder[PlayerHand]

  implicit val colorDecoder = deriveDecoder[Color]

  implicit val xaxisDecoder       = Decoder.decodeInt.map(XAxis.apply)
  implicit val yaxisDecoder       = Decoder.decodeInt.map(YAxis.apply)
  implicit val coordinatesDecoder = deriveDecoder[Coordinates]

  implicit val arrowDecoder          = deriveDecoder[Arrow]
  implicit val playerGameMoveDecoder = deriveDecoder[GameMovement]
  implicit val playerMovementDecoder = deriveDecoder[PlayerMove]
  implicit val gameEventDecoder      = deriveDecoder[GameEvent]
}
