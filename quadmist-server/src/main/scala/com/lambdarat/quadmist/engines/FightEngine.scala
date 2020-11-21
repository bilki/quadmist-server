package com.lambdarat.quadmist.engines

import com.lambdarat.quadmist.common.domain.BattleClass.{Assault, Flexible, Magical, Physical}
import com.lambdarat.quadmist.common.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}
import com.lambdarat.quadmist.common.domain._

import scala.math.{max, min}
import scala.util.Random

trait FightEngine {

  private def arrowCombat(
      attacker: Attacker,
      defender: Defender,
      side: Arrow,
      gameSettings: Settings
  ): Fight = {
    val cardMaxLevel              = gameSettings.cardMaxLevel.toInt
    def hitPoints(stat: Int): Int = stat * cardMaxLevel

    // Battle maths
    def statVs(atkStat: Int, defStat: Int): (Int, Int) = {
      val p1atk = hitPoints(atkStat) + Random.nextInt(cardMaxLevel)
      val p2def = hitPoints(defStat) + Random.nextInt(cardMaxLevel)
      (p1atk - Random.nextInt(p1atk + 1), p2def - Random.nextInt(p2def + 1))
    }

    val atkCard = attacker.card
    val defCard = defender.card

    val (atkStat, defStat) = atkCard.bclass match {
      case Physical => (atkCard.power.toInt, defCard.pdef.toInt)
      case Magical  => (atkCard.power.toInt, defCard.mdef.toInt)
      case Flexible => (atkCard.power.toInt, min(defCard.pdef.toInt, defCard.mdef.toInt))
      case Assault  =>
        (
          max(max(atkCard.power.toInt, atkCard.pdef.toInt), atkCard.mdef.toInt),
          min(min(defCard.power.toInt, defCard.pdef.toInt), defCard.mdef.toInt)
        )
    }

    val (atkScore, defScore) = statVs(atkStat, defStat)

    Fight(
      attacker,
      defender,
      side,
      AttackerPoints(atkScore),
      DefenderPoints(defScore),
      AttackerWins(atkScore > defScore)
    )
  }

  /** Challenge another card.
    *
    * @param attacker attacking card
    * @param defender enemy card
    * @param side     location of the enemy card
    * @return a fight result
    */
  def fight(
      attacker: Attacker,
      defender: Defender,
      side: Arrow
  )(implicit gameSettings: Settings): Either[String, Fight] = {
    // Fight!!
    lazy val fightResult = if (defender.card.arrows.contains(side.opposite)) {
      arrowCombat(attacker, defender, side, gameSettings)
    } else {
      // Instant win, no defender arrow
      Fight(attacker, defender, side, AttackerPoints(0), DefenderPoints(0), AttackerWins(true))
    }

    Either.cond(
      attacker.card.arrows.contains(side),
      fightResult,
      s"Attacker does not contain $side arrow "
    )
  }

}
