package com.lambdarat.quadmist.repository

import com.lambdarat.quadmist.domain.Player
import com.lambdarat.quadmist.game.GameError.InvalidPlayer
import com.lambdarat.quadmist.repository.dto.PlayerDTO

import memeid4s.UUID
import memeid4s.cats.implicits._

import scala.collection.concurrent.TrieMap

import cats.effect.IO

trait PlayerRepository[F[_]] {
  def getPlayer(id: Player.Id): F[PlayerDTO]
  def createPlayer(name: Player.Name): F[Player.Id]
  def savePlayer(player: PlayerDTO): F[Boolean]
}

object PlayerRepository {
  def apply[F[_]](implicit pr: PlayerRepository[F]): PlayerRepository[F] = pr

  implicit val memRepo = new PlayerRepository[IO] {
    val players: TrieMap[Player.Id, PlayerDTO] = TrieMap.empty

    override def getPlayer(id: Player.Id): IO[PlayerDTO] =
      IO.fromOption(players.get(id))(InvalidPlayer(id))

    override def createPlayer(name: Player.Name): IO[Player.Id] =
      for {
        playerId <- UUID.random[IO].map(Player.Id.apply)
        _        <- IO(players.put(playerId, PlayerDTO(playerId, Player(name))))
      } yield playerId

    override def savePlayer(player: PlayerDTO): IO[Boolean] =
      IO(players.putIfAbsent(player.id, player).isEmpty)
  }
}
