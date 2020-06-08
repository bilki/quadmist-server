package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.BattleClass.{Assault, Flexible, Magical, Physical}
import com.lambdarat.quadmist.domain.Board.Hand
import com.lambdarat.quadmist.domain.Card.{MagicalDef, PhysicalDef, Power}
import com.lambdarat.quadmist.engines.{GameSettings, board}

import memeid4s.UUID
import org.scalacheck.{Arbitrary, Gen}

object ModelGens {

  private val cardClassGenerator: Gen[CardClass] =
    Gen.alphaStr.map(name => CardClass(CardClass.Name(name)))

  private val battleClassGenerator: Gen[BattleClass] =
    Gen.oneOf(Physical, Magical, Flexible, Assault)

  private val arrowsGenerator: Gen[List[Arrow]] = Gen.someOf(Arrow.values).map(_.toList)

  val invalidArrowsGenerator: Gen[List[Arrow]] = Gen.choose(1, Arrow.MAX_ARROWS + 1) flatMap {
    size =>
      Gen.listOfN(size, Gen.oneOf(Arrow.values))
  }

  implicit val arrows: Arbitrary[List[Arrow]] = Arbitrary(arrowsGenerator)

  private def cardGenerator(implicit gameSettings: GameSettings): Gen[Card] =
    for {
      ownerId     <- Gen.const(UUID.V4.random).map(Player.Id.apply)
      cardClass   <- cardClassGenerator
      power       <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1).map(Power.apply)
      battleClass <- battleClassGenerator
      pdef        <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1).map(PhysicalDef.apply)
      mdef        <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1).map(MagicalDef.apply)
      arrows      <- arrowsGenerator
      maybeCard    = Card(ownerId, cardClass.id, power, battleClass, pdef, mdef, arrows)
      card        <- maybeCard.fold[Gen[Card]](Gen.fail)(Gen.const)
    } yield card

  implicit def cards(implicit gameSettings: GameSettings): Arbitrary[Card] =
    Arbitrary(cardGenerator)

  private def handGenerator(implicit gameSettings: GameSettings): Gen[Hand] =
    Gen.containerOfN[Set, Card](gameSettings.MAX_HAND_CARDS, cardGenerator).map(Hand.apply)

  private def boardGenerator(implicit
      boardSettings: BoardSettings,
      gameSettings: GameSettings
  ): Gen[Board] =
    for {
      redHand  <- handGenerator
      blueHand <- handGenerator
    } yield board.random(redHand, blueHand, boardSettings)

  implicit def boards(implicit
      boardSettings: BoardSettings,
      gameSettings: GameSettings
  ): Arbitrary[Board] = {
    Arbitrary(boardGenerator)
  }

  private val playerIdGenerator: Gen[Player.Id] = Gen.const(Player.Id(UUID.V4.random))

  implicit val playerIds: Arbitrary[Player.Id] = Arbitrary(playerIdGenerator)
}
