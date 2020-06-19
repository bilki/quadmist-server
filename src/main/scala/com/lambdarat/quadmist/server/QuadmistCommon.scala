package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.game.{GameInfo, TurnState}

import fs2.Stream
import fs2.concurrent.Topic

import cats.effect.concurrent.MVar

object QuadmistCommon {
  type Turns[F[_]] = Topic[F, TurnState]
  type Game[F[_]]  = MVar[F, GameInfo]

  implicit class StreamConversionOps[F[_], O](f: F[O]) {
    def toStream: Stream[F, O] = Stream.eval(f)
  }
}
