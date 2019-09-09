package net.server.channel.packet

class UseHammerPacket(private var _position: Short, private var _itemId: Int, private var _slot: Short) extends AbstractUseCashItemPacket(_position, _itemId) {
  def slot: Int = _slot
}
