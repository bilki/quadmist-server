package com.lambdarat.quadmist.game

import com.lambdarat.quadmist.domain.Card

case class InitialHand(
    first: Card.Id,
    second: Card.Id,
    third: Card.Id,
    fourth: Card.Id,
    fifth: Card.Id
)
