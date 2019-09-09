package net.server.channel.packet

class UseItemTagPacket(private var _position: Short, private var _itemId: Int, private var _slot: Short) extends AbstractUseCashItemPacket(_position, _itemId) {
  def slot: Int = _slot
}
