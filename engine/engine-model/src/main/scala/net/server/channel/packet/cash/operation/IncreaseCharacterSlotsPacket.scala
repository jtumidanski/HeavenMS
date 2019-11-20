package net.server.channel.packet.cash.operation

class IncreaseCharacterSlotsPacket(private var _action: Int, private var _cash: Int, private var _itemId: Int) extends BaseCashOperationPacket(_action = _action) {
  def cash: Int = _cash

  def itemId: Int = _itemId
}
