package net.server.channel.packet.wedding

class TakeRegistryItemsPacket(private var _mode: Byte, private var _itemPosition: Int) extends BaseWeddingPacket(_mode = _mode) {
  def itemPosition: Int = _itemPosition
}
