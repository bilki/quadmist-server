package com.lambdarat.quadmist.server

import com.lambdarat.quadmist.common.domain.Arrow._
import com.lambdarat.quadmist.common.domain.Card.{MagicalDef, PhysicalDef, Power}
import com.lambdarat.quadmist.common.domain.{BattleClass, Card, CardClass, Player}

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

import io.chrisdavenport.fuuid.FUUID

import scala.io.Source

import cats.effect.Sync
import cats.implicits._

object InitSample {
  private val playerOneId = Player.Id(FUUID.fuuid("7919293f-88b9-411e-9920-57bff4c5a8cf"))
  private val playerTwoId = Player.Id(FUUID.fuuid("7d588a91-c7a1-4a02-8f78-c3929c119849"))

  lazy val players = List(
    playerOneId -> Player.Name("Roberto"),
    playerTwoId -> Player.Name("Sergio")
  )

  lazy val cards = List(
    Card.Id(FUUID.fuuid("353dc0fe-e3d5-4d09-bcac-951d60865ea2")) -> (
      playerOneId,
      CardClass.Id(FUUID.fuuid("7006d692-95ce-440f-9caa-ee4e13f0b977")),
      Card(
        power = Power(1),
        bclass = BattleClass.Assault,
        pdef = PhysicalDef(2),
        mdef = MagicalDef(3),
        arrows = List(N, NE)
      )
    ),
    Card.Id(FUUID.fuuid("cd098048-3517-4f39-a25a-4a1e5f0c3e7e")) -> (
      playerOneId,
      CardClass.Id(FUUID.fuuid("1bbc2187-6616-491b-9e88-4ff1d7caea9e")),
      Card(
        power = Power(2),
        bclass = BattleClass.Magical,
        pdef = PhysicalDef(1),
        mdef = MagicalDef(1),
        arrows = List(N, NE, E, SW)
      )
    ),
    Card.Id(FUUID.fuuid("12ed5740-d894-4f92-a0b6-0ae494acbdbc")) -> (
      playerOneId,
      CardClass.Id(FUUID.fuuid("202e90b9-3f77-42b4-b481-b7a4c54bac3c")),
      Card(
        power = Power(3),
        bclass = BattleClass.Physical,
        pdef = PhysicalDef(5),
        mdef = MagicalDef(2),
        arrows = List(SW, W)
      )
    ),
    Card.Id(FUUID.fuuid("c88ddb5f-a085-4607-8476-c9eb5618f3f4")) -> (
      playerOneId,
      CardClass.Id(FUUID.fuuid("1eb7b980-9802-4d62-a3ef-9607a4902327")),
      Card(
        power = Power(4),
        bclass = BattleClass.Magical,
        pdef = PhysicalDef(8),
        mdef = MagicalDef(3),
        arrows = List(NW, N, NE)
      )
    ),
    Card.Id(FUUID.fuuid("b1ab1509-0c2b-404b-a968-2e49b90463d3")) -> (
      playerOneId,
      CardClass.Id(FUUID.fuuid("0c892ecb-77ce-4eba-821b-77170adac5ba")),
      Card(
        power = Power(5),
        bclass = BattleClass.Physical,
        pdef = PhysicalDef(1),
        mdef = MagicalDef(3),
        arrows = List(SE, E, W)
      )
    ),
    Card.Id(FUUID.fuuid("132cd988-0291-43f2-a846-65498546d09f")) -> (
      playerTwoId,
      CardClass.Id(FUUID.fuuid("f1469ed6-7b6a-4000-b55c-aed5476feb79")),
      Card(
        power = Power(6),
        bclass = BattleClass.Physical,
        pdef = PhysicalDef(6),
        mdef = MagicalDef(2),
        arrows = List(W, SW, NW)
      )
    ),
    Card.Id(FUUID.fuuid("19e7ba53-9393-43c2-b5e2-6e814c466783")) -> (
      playerTwoId,
      CardClass.Id(FUUID.fuuid("4cb0e280-9cc5-485f-bc75-162848eeb222")),
      Card(
        power = Power(7),
        bclass = BattleClass.Magical,
        pdef = PhysicalDef(3),
        mdef = MagicalDef(4),
        arrows = List(N, S)
      )
    ),
    Card.Id(FUUID.fuuid("ed94ad8b-90c3-41fd-91c1-0008f621b5a0")) -> (
      playerTwoId,
      CardClass.Id(FUUID.fuuid("a7befb5b-9f95-4e86-a47b-51cb6e6f9279")),
      Card(
        power = Power(8),
        bclass = BattleClass.Assault,
        pdef = PhysicalDef(6),
        mdef = MagicalDef(5),
        arrows = List(W, SW, E, N)
      )
    ),
    Card.Id(FUUID.fuuid("b8239040-638b-4039-ac9a-390fc3019bfd")) -> (
      playerTwoId,
      CardClass.Id(FUUID.fuuid("b6610cc2-852e-496b-8012-2d4a73f037b3")),
      Card(
        power = Power(9),
        bclass = BattleClass.Magical,
        pdef = PhysicalDef(9),
        mdef = MagicalDef(12),
        arrows = List(NW, SE)
      )
    ),
    Card.Id(FUUID.fuuid("0774c28e-795c-4078-b0a6-98864a0cf750")) -> (
      playerTwoId,
      CardClass.Id(FUUID.fuuid("f48ca163-da02-461f-a30e-72f5778b29b9")),
      Card(
        power = Power(10),
        bclass = BattleClass.Physical,
        pdef = PhysicalDef(7),
        mdef = MagicalDef(3),
        arrows = List(N, NE, S, SE)
      )
    )
  )

  import com.lambdarat.quadmist.common.codecs._
  import io.circe.parser.decode

  case class CardClassEntry(id: CardClass.Id, name: CardClass.Name)

  implicit val cardClassEntryDecoder: Decoder[CardClassEntry] = deriveDecoder[CardClassEntry]

  def loadCardClasses[F[_]: Sync]: F[List[CardClassEntry]] =
    for {
      ccContent   <- Sync[F].delay(Source.fromResource("cardClasses.json").mkString)
      cardClasses <- Sync[F].fromEither(decode[List[CardClassEntry]](ccContent))
    } yield cardClasses

}
