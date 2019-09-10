package net.server.channel.packet.cash

class UseSpResetPacket(private var _position: Short, private var _itemId: Int, private var _to: Int, private var _from: Int) extends AbstractUseCashItemPacket(_position, _itemId) {
  def to: Int = _to

  def from: Int = _from
}
