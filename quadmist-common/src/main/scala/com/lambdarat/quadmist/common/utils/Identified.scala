package com.lambdarat.quadmist.common.utils

case class Identified[I, E](id: I, entity: E)

object Identified {
  implicit final class WithIdOps[E](target: E) {
    def withId[I](id: I): Identified[I, E] = Identified(id, target)
  }
}
