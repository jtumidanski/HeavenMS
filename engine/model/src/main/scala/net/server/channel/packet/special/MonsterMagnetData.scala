package net.server.channel.packet.special

class MonsterMagnetData(private var _monsterId: Int, private var _success: Byte) {
  def monsterId: Int = _monsterId

  def success: Byte = _success
}
