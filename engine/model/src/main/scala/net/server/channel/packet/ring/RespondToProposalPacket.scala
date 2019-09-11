package net.server.channel.packet.ring

class RespondToProposalPacket(private var _mode: Byte, private var _accepted: Boolean, private var _name: String, private var _itemId: Int) extends BaseRingPacket(_mode = _mode) {
  def accepted: Boolean = _accepted

  def name: String = _name

  def itemId: Int = _itemId
}
