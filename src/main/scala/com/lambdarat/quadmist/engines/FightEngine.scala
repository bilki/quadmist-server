package com.lambdarat.quadmist.engines

import com.lambdarat.quadmist.domain.BattleClass.{Assault, Flexible, Magical, Physical}
import com.lambdarat.quadmist.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}
import com.lambdarat.quadmist.domain._

import scala.math.{max, min}
import scala.util.Random

trait FightEngine {

  private def arrowCombat(attacker: Card, defender: Card, gameSettings: GameSettings): Fight = {
    def hitPoints(stat: Int): Int = stat * gameSettings.CARD_MAX_LEVEL

    // Battle maths
    def statVs(atkStat: Int, defStat: Int): (Int, Int) = {
      val p1atk = hitPoints(atkStat) + Random.nextInt(gameSettings.CARD_MAX_LEVEL)
      val p2def = hitPoints(defStat) + Random.nextInt(gameSettings.CARD_MAX_LEVEL)
      (p1atk - Random.nextInt(p1atk + 1), p2def - Random.nextInt(p2def + 1))
    }

    val (atkStat, defStat) = attacker.bclass match {
      case Physical => (attacker.power.toInt, defender.pdef.toInt)
      case Magical  => (attacker.power.toInt, defender.mdef.toInt)
      case Flexible => (attacker.power.toInt, min(defender.pdef.toInt, defender.mdef.toInt))
      case Assault  =>
        (
          max(max(attacker.power.toInt, attacker.pdef.toInt), attacker.mdef.toInt),
          min(min(defender.power.toInt, defender.pdef.toInt), defender.mdef.toInt)
        )
    }

    val (atkScore, defScore) = statVs(atkStat, defStat)

    Fight(
      attacker,
      defender,
      AttackerPoints(atkScore),
      DefenderPoints(defScore),
      AttackerWins(atkScore > defScore)
    )
  }

  /**
    * Challenge another card.
    *
    * @param attacker attacking card
    * @param defender enemy card
    * @param side     location of the enemy card
    * @return a fight result
    */
  def fight(
      attacker: Card,
      defender: Card,
      side: Arrow
  )(implicit gameSettings: GameSettings): Either[String, Fight] = {
    // Fight!!
    val fightResult = if (defender.arrows.contains(side.opposite)) {
      arrowCombat(attacker, defender, gameSettings)
    } else {
      // Instant win, no defender arrow
      Fight(attacker, defender, AttackerPoints(0), DefenderPoints(0), AttackerWins(true))
    }

    Either.cond(
      attacker.arrows.contains(side),
      fightResult,
      s"Attacker does not contain $side arrow "
    )
  }

}
