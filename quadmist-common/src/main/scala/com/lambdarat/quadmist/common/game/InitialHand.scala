package com.lambdarat.quadmist.common.game

import com.lambdarat.quadmist.common.domain.Card

/**
  * Represents a standard 5-cards hand set of identifiers.
  */
final case class InitialHand(
    c1: Card.Id,
    c2: Card.Id,
    c3: Card.Id,
    c4: Card.Id,
    c5: Card.Id
)
