package com.lambdarat.quadmist.domain

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
    attacker: Card.Id,
    defender: Card.Id,
    atkPoints: Int,
    defPoints: Int,
    atkWinner: Boolean,
    dateTime: Instant = Instant.now
)

object Fight {

  case class Id(value: String) extends AnyVal

}
