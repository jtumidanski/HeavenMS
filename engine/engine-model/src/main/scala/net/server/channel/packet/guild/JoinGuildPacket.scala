package net.server.channel.packet.guild

class JoinGuildPacket(private var _type: Byte, private var _guildId: Int, private var _playerId: Int) extends BaseGuildOperationPacket(_type = _type) {
  def guildId: Int = _guildId

  def playerId: Int = _playerId
}

