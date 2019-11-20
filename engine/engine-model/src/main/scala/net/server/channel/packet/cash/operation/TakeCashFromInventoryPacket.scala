package net.server.channel.packet.cash.operation

class TakeCashFromInventoryPacket(private var _action: Int, private var _itemId: Int) extends BaseCashOperationPacket(_action = _action) {
  def itemId: Int = _itemId
}
