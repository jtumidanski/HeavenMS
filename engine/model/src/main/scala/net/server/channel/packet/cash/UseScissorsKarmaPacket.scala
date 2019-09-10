package net.server.channel.packet.cash

class UseScissorsKarmaPacket(private var _position: Short, private var _itemId: Int, private var _itemType: Byte, private var _slot: Short) extends AbstractUseCashItemPacket(_position, _itemId) {
  def itemType: Byte = _itemType

  def slot: Int = _slot
}
