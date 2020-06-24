package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Card.{MagicalDef, PhysicalDef, Power}

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
  * @param power offensive stat
  * @param bclass battle class
  * @param pdef physical defense stat
  * @param mdef magical defense stat
  * @param arrows list of atk/def arrows
  */
final case class Card(
    power: Power,
    bclass: BattleClass,
    pdef: PhysicalDef,
    mdef: MagicalDef,
    arrows: List[Arrow]
)

object Card {
  @newtype case class Power(toInt: Int)
  @newtype case class PhysicalDef(toInt: Int)
  @newtype case class MagicalDef(toInt: Int)
  @newtype case class Id(toUUID: UUID)

  def create(
      power: Power,
      bclass: BattleClass,
      pdef: PhysicalDef,
      mdef: MagicalDef,
      arrows: List[Arrow]
  )(implicit gameSettings: Settings): Option[Card] = {
    val maxLevel = gameSettings.cardMaxLevel.toInt

    val validCard =
      power.toInt < maxLevel && pdef.toInt < maxLevel && mdef.toInt < maxLevel &&
        Arrow.checkArrows(arrows)

    Option.when(validCard)(
      new Card(power, bclass, pdef, mdef, arrows)
    )
  }
}
