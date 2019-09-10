package net.server.channel.packet.interaction

class BaseCreatePlayerInteractionPacket(private var _mode: Byte, private var _createType: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
  def createType: Byte = _createType
}