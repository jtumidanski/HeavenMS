package net.server.channel.packet.storage

class StorePacket(private var _mode: Byte, private var _slot: Short, private var _itemId: Int, private var _quantity: Short) extends BaseStoragePacket(_mode = _mode) {
  def slot: Short = _slot

  def itemId: Int = _itemId

  def quantity: Short = _quantity
}
