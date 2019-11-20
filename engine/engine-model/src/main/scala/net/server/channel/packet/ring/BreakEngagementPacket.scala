package net.server.channel.packet.ring

class BreakEngagementPacket(private var _mode: Byte, private var _itemId: Int) extends BaseRingPacket(_mode = _mode) {
  def itemId: Int = _itemId
}
