package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.BattleClass.{Assault, Flexible, Magical, Physical}
import com.lambdarat.quadmist.engines.{GameSettings, board}

import org.scalacheck.{Arbitrary, Gen}

import java.net.URL

object ModelGens {

  private val urlProtocol = "http://"

  private val cardClassGenerator: Gen[CardClass] = for {
    id   <- Gen.choose(0, Int.MaxValue)
    name <- Gen.alphaStr
    img  <- Gen.alphaStr
  } yield {
    CardClass(CardClass.Name(name), new URL(urlProtocol + img), Some(CardClass.Id(id)))
  }

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
      id          <- Gen.choose(0, Int.MaxValue)
      ownerId     <- Gen.choose(0, Int.MaxValue)
      cardClass   <- cardClassGenerator
      power       <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1)
      battleClass <- battleClassGenerator
      pdef        <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1)
      mdef        <- Gen.choose(0, gameSettings.CARD_MAX_LEVEL - 1)
      arrows      <- arrowsGenerator
    } yield {
      Card(
        Player.Id(ownerId),
        cardClass.id.get,
        power,
        battleClass,
        pdef,
        mdef,
        arrows.toList,
        Some(Card.Id(id))
      )
    }

  implicit def cards(implicit gameSettings: GameSettings): Arbitrary[Card] =
    Arbitrary(cardGenerator)

  private def handGenerator(implicit gameSettings: GameSettings): Gen[Set[Card]] =
    Gen.containerOfN[Set, Card](gameSettings.MAX_HAND_CARDS, cardGenerator)

  private def boardGenerator(
      implicit boardSettings: BoardSettings,
      gameSettings: GameSettings
  ): Gen[Board] =
    for {
      redHand  <- handGenerator
      blueHand <- handGenerator
    } yield board.random(redHand, blueHand, boardSettings)

  implicit def boards(
      implicit boardSettings: BoardSettings,
      gameSettings: GameSettings
  ): Arbitrary[Board] = {
    Arbitrary(boardGenerator)
  }

  private val playerIdGenerator: Gen[Player.Id] = Gen.choose(0, Int.MaxValue).map(Player.Id.apply)

  implicit val playerIds: Arbitrary[Player.Id] = Arbitrary(playerIdGenerator)
}
