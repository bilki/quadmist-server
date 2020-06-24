package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.CardClass.Name

import io.estatico.newtype.macros.newtype
import memeid4s.UUID

/**
  * Card class.
  *
  * @param name card name
  */
final case class CardClass(name: Name)

object CardClass {
  @newtype case class Id(toUUID: UUID)
  @newtype case class Name(toStr: String)
}
