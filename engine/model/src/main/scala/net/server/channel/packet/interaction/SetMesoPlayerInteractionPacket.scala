package net.server.channel.packet.interaction

class SetMesoPlayerInteractionPacket(private var _mode: Byte, private var _amount: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def amount: Int = _amount
}