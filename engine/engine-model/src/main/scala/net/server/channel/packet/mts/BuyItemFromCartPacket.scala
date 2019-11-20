package net.server.channel.packet.mts

class BuyItemFromCartPacket(private var _available: Boolean, private var _operation: Byte, private var _itemId: Int) extends BaseMTSPacket(_available = _available, _operation = _operation) {
  def itemId: Int = _itemId
}

