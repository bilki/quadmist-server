package com.lambdarat.quadmist.domain

import java.net.URL

/**
  * Card class.
  *
  * @param name card name
  * @param img url of the image for this card
  * @param id unique identifier of this card class
  */
final case class CardClass(name: CardClass.Name, img: URL, id: Option[CardClass.Id] = None)

object CardClass {

  case class Id(value: Int) extends AnyVal

  case class Name(value: String) extends AnyVal

}
