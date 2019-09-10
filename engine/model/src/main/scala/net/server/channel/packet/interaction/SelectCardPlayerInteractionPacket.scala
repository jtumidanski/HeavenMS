package net.server.channel.packet.interaction

class SelectCardPlayerInteractionPacket(private var _mode: Byte, private var _turn: Int, private var _slot: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def turn: Int = _turn

  def slot: Int = _slot
}