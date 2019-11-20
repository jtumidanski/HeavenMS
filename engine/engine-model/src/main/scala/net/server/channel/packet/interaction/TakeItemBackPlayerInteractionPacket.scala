package net.server.channel.packet.interaction

class TakeItemBackPlayerInteractionPacket(private var _mode: Byte, private var _slot: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def slot: Int = _slot
}