package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.ModelGens._

class CardSpec extends ModelSpec {

  "A card" when {
    implicit val defaultGameSettings: Settings = Settings.default

    "created" should {
      "have correct power level according to game settings" in {
        forAll { card: Card =>
          card.power.toInt should be < defaultGameSettings.cardMaxLevel.toInt
        }
      }

      "have correct physical defense level according to game settings" in {
        forAll { card: Card =>
          card.pdef.toInt should be < defaultGameSettings.cardMaxLevel.toInt
        }
      }

      "have correct magic defense level according to game settings" in {
        forAll { card: Card =>
          card.mdef.toInt should be < defaultGameSettings.cardMaxLevel.toInt
        }
      }

      "have a number of arrows less or equal than max arrows" in {
        forAll { card: Card =>
          card.arrows.size should be <= Arrow.MAX_ARROWS
        }
      }

      "have a list of distinct arrows" in {
        forAll { card: Card =>
          card.arrows.distinct should be(card.arrows)
        }
      }
    }
  }

}
