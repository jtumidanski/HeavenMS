package net.server.channel.packet.guild

class ChangeGuildNoticePacket(private var _type: Byte, private var _notice: String) extends BaseGuildOperationPacket(_type = _type) {
  def notice: String = _notice
}
