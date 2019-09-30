package net.server.guild

import scala.jdk.CollectionConverters._

class MapleAlliance(var name: String, var id: Int) {
  private var _guilds: List[Int] = List()

  var notice: String = ""

  var capacity: Int = 0

  var rankTitles: Array[String] = Array("Master", "Jr. Master", "Member", "Member", "Member")

  def removeGuild(guildId: Int): Unit = {
    _guilds.synchronized {
      _guilds = _guilds.filter(id => id != guildId)
    }
  }

  def addGuild(guildId: Int): Unit = {
    _guilds.synchronized {
      if (_guilds.size == capacity || _guilds.contains(guildId)) {
        return
      }
      _guilds = _guilds.appended(guildId)
    }
  }

  def rankTitle(rank: Int): String = {
    rankTitles(rank - 1)
  }

  def guilds: java.util.List[Integer] = {
    _guilds.map(i => i: java.lang.Integer).asJava
  }

  def increaseCapacity(increase: Int): Unit = capacity += increase
}
