package net.server.channel.packet.interaction

class BanPlayerPlayerInteractionPacket(private var _mode: Byte, private var _name: String) extends BasePlayerInteractionPacket(_mode = _mode) {
  def name: String = _name
}