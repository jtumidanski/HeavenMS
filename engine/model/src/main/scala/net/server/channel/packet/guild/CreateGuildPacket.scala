package net.server.channel.packet.guild

class CreateGuildPacket(private var _type: Byte, private var _name: String) extends BaseGuildOperationPacket(_type = _type) {
  def name: String = _name
}
