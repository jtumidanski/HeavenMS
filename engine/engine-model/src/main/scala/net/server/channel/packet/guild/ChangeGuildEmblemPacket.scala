package net.server.channel.packet.guild

class ChangeGuildEmblemPacket(private var _type: Byte, private var _background: Short, private var _backgroundColor: Byte, private var _logo: Short, private var _logoColor: Byte) extends BaseGuildOperationPacket(_type = _type) {
  def background: Short = _background

  def backgroundColor: Byte = _backgroundColor

  def logo: Short = _logo

  def logoColor: Byte = _logoColor
}

