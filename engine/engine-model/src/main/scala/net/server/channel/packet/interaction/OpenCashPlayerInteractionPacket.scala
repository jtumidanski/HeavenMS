package net.server.channel.packet.interaction

class OpenCashPlayerInteractionPacket(private var _mode: Byte, private var _birthday: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def birthday: Int = _birthday
}