package net.server.channel.packet.cash.operation

class IncreaseStorageSlotsLarge(private var _action: Int, private var _cash: Int, private var _mode: Byte, private var _itemId: Int) extends BaseCashOperationPacket(_action = _action) {
  def cash: Int = _cash

  def mode: Byte = _mode

  def itemId: Int = _itemId
}
