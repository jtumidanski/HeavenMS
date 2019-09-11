package net.server.channel.packet.ring

class OpenWeddingInvitationPacket(private var _mode: Byte, private var _slot: Byte, private var _invitationId: Int) extends BaseRingPacket(_mode = _mode) {
  def slot: Byte = _slot

  def invitationId: Int = _invitationId
}
