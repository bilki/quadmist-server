package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.ModelGens._
import com.lambdarat.quadmist.engines.GameSettings

class CardSpec extends ModelSpec {

  "A card" when {
    implicit val defaultGameSettings: GameSettings = new GameSettings(
      CARD_MAX_LEVEL = 16,
      MAX_HAND_CARDS = 5
    )

    "created" should {
      "have correct power level according to game settings" in {
        forAll { card: Card =>
          card.power.toInt should be < defaultGameSettings.CARD_MAX_LEVEL
        }
      }

      "have correct physical defense level according to game settings" in {
        forAll { card: Card =>
          card.pdef.toInt should be < defaultGameSettings.CARD_MAX_LEVEL
        }
      }

      "have correct magic defense level according to game settings" in {
        forAll { card: Card =>
          card.mdef.toInt should be < defaultGameSettings.CARD_MAX_LEVEL
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
