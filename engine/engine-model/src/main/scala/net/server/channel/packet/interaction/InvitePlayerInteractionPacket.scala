package net.server.channel.packet.interaction

class InvitePlayerInteractionPacket(private var _mode: Byte, private var _otherCharacterId: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def otherCharacterId: Int = _otherCharacterId
}
