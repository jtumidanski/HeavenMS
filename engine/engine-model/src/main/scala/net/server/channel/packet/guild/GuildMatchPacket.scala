package net.server.channel.packet.guild

class GuildMatchPacket(private var _type: Byte, private var _result: Boolean) extends BaseGuildOperationPacket(_type = _type) {
  def result: Boolean = _result
}
