package com.lambdarat.quadmist.domain

import com.lambdarat.quadmist.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

import java.time.Instant

/**
  * Result of a card fight.
  *
  * @param attacker card attacking
  * @param defender card defending
  * @param atkPoints points of the attack
  * @param defPoints points of the defense
  * @param atkWinner true if attacker was the winner of the fight
  */
final case class Fight(
    attacker: Card,
    defender: Card,
    atkPoints: AttackerPoints,
    defPoints: DefenderPoints,
    atkWinner: AttackerWins,
    dateTime: Instant = Instant.now
)

object Fight {
  @newtype case class AttackerPoints(toInt: Int)
  @newtype case class DefenderPoints(toInt: Int)
  @newtype case class AttackerWins(toBool: Boolean)
  @newtype case class Id(toUUID: UUID)
}
