package net.server.channel.packet.guild

class ChangeGuildRankPacket(private var _type: Byte, private var _playerId: Int, private var _rank: Byte) extends BaseGuildOperationPacket(_type = _type) {
  def playerId: Int = _playerId

  def rank: Byte = _rank
}
