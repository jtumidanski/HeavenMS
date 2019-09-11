package net.server.channel.packet.mts

class PlaceItemForSalePacket(private var _available: Boolean, private var _operation: Byte, private var _itemId: Int, private var _quantity: Short, private var _price: Int, private var _slot: Short) extends BaseMTSPacket(_available = _available, _operation = _operation) {
  def itemId: Int = _itemId

  def quantity: Short = _quantity

  def price: Int = _price

  def slot: Short = _slot
}
