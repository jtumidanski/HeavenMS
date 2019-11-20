package net.server.channel.packet.cash.operation

class WorldTransferPacket(private var _action: Int, private var _itemId: Int, private var _newWorldId: Int) extends BaseCashOperationPacket(_action = _action) {
  def itemId: Int = _itemId

  def newWorldId: Int = _newWorldId
}
