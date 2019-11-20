package net.server.channel.packet.wedding

class AddRegistryItemPacket(private var _mode: Byte, private var _slot: Short, private var _itemId: Int, private var _quantity: Short) extends BaseWeddingPacket(_mode = _mode) {
  def slot: Short = _slot

  def itemId: Int = _itemId

  def quantity: Short = _quantity
}
