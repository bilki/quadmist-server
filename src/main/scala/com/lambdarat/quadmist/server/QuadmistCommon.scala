package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.game.{GameError, GameInfo, TurnState}
import com.lambdarat.quadmist.utils.Identified

import fs2.Stream
import fs2.concurrent.Topic

import cats.effect.concurrent.MVar

object QuadmistCommon {
  type EventOutcome = Either[Identified[Player.Id, GameError], TurnState]
  type Turns[F[_]]  = Topic[F, EventOutcome]
  type Game[F[_]]   = MVar[F, GameInfo]

  implicit class StreamConversionOps[F[_], O](f: F[O]) {
    def toStream: Stream[F, O] = Stream.eval(f)
  }
}
