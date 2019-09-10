package net.server.channel.packet.guild

class ChangeGuildRankAndTitlePacket(private var _type: Byte, private var _ranks: Array[String]) extends BaseGuildOperationPacket(_type = _type) {
  def ranks: Array[String] = _ranks
}
