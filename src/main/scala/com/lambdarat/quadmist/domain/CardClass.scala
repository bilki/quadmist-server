package com.lambdarat.quadmist.domain

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Card class.
  *
  * @param name card name
  * @param id unique identifier of this card class
  */
final case class CardClass private (name: CardClass.Name, id: CardClass.Id)

object CardClass {
  def apply(name: Name): CardClass = {
    val cardClassId = Id(UUID.V4.random)
    CardClass(name, cardClassId)
  }

  @newtype case class Id(toUUID: UUID)
  @newtype case class Name(toStr: String)
}
