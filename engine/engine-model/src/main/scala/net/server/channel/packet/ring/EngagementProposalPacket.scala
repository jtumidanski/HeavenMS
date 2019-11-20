package net.server.channel.packet.ring

class EngagementProposalPacket(private var _mode: Byte, private var _name: String, private var _itemId: Int) extends BaseRingPacket(_mode = _mode) {
  def name: String = _name

  def itemId: Int = _itemId
}
