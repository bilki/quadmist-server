package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.common.domain.Player
import com.lambdarat.quadmist.common.game.{GameError, TurnState}
import com.lambdarat.quadmist.common.utils.Identified
import com.lambdarat.quadmist.game.GameInfo

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
