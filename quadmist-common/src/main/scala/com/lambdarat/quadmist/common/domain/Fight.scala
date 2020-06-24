package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.Fight.{AttackerPoints, AttackerWins, DefenderPoints}

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

final case class Attacker(card: Card, coordinates: Coordinates)
final case class Defender(card: Card, coordinates: Coordinates)

/**
  * Result of a card fight.
  *
  * @param attacker card attacking
  * @param defender card defending
  * @param target direction of the attack
  * @param atkPoints points of the attack
  * @param defPoints points of the defense
  * @param atkWinner true if attacker was the winner of the fight
  */
final case class Fight(
    attacker: Attacker,
    defender: Defender,
    target: Arrow,
    atkPoints: AttackerPoints,
    defPoints: DefenderPoints,
    atkWinner: AttackerWins
)

object Fight {
  @newtype case class AttackerPoints(toInt: Int)
  @newtype case class DefenderPoints(toInt: Int)
  @newtype case class AttackerWins(toBool: Boolean)
  @newtype case class Id(toUUID: UUID)
}
