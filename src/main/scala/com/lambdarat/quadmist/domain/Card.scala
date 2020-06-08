package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.Card.{MagicalDef, PhysicalDef, Power}

import enumeratum._
import io.estatico.newtype.macros.newtype
import memeid.UUID

/**
  * Battle class of the card.
  *
  * - Physical attacks physical def stat
  * - Magical attacks magical def stat
  * - Flexible attacks lowest def stat
  * - Assault attacks the lowest stat
  *
  * Reference: [[http://finalfantasy.wikia.com/wiki/Tetra_Master_(Minigame)#Battle_class_stat Final Fantasy Wiki]]
  */
sealed trait BattleClass extends EnumEntry { def uiChar: Char }

object BattleClass extends Enum[BattleClass] {
  val values = findValues

  case object Physical extends BattleClass { val uiChar: Char = 'P' }
  case object Magical  extends BattleClass { val uiChar: Char = 'M' }
  case object Flexible extends BattleClass { val uiChar: Char = 'X' }
  case object Assault  extends BattleClass { val uiChar: Char = 'A' }
}

/**
  * Unique card instance.
  *
  * @param ownerId player identifier
  * @param cardType type of card
  * @param power offensive stat
  * @param bclass battle class
  * @param pdef physical defense stat
  * @param mdef magical defense stat
  * @param arrows list of atk/def arrows
  * @param id unique identifier
  */
final case class Card private (
    ownerId: Player.Id,
    cardType: CardClass.Id,
    power: Power,
    bclass: BattleClass,
    pdef: PhysicalDef,
    mdef: MagicalDef,
    arrows: List[Arrow],
    id: Card.Id
)

object Card {
  @newtype case class Power(toInt: Int)
  @newtype case class PhysicalDef(toInt: Int)
  @newtype case class MagicalDef(toInt: Int)

  def apply(
      ownerId: Player.Id,
      cardType: CardClass.Id,
      power: Power,
      bclass: BattleClass,
      pdef: PhysicalDef,
      mdef: MagicalDef,
      arrows: List[Arrow]
  ): Option[Card] = {
    //  require(power < gameSettings.CARD_MAX_LEVEL)
    //  require(pdef < gameSettings.CARD_MAX_LEVEL)
    //  require(mdef < gameSettings.CARD_MAX_LEVEL)

    Option.when(Arrow.checkArrows(arrows)) {
      val cardId = Card.Id(UUID.V4.random)
      new Card(ownerId, cardType, power, bclass, pdef, mdef, arrows, cardId)
    }
  }

  @newtype case class Id(toUUID: UUID)
}
