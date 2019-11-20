package net.server.channel.packet.cash.operation

class IncreaseStorageSlotsSmall(private var _action: Int, private var _cash: Int, private var _mode: Byte) extends BaseCashOperationPacket(_action = _action) {
  def cash: Int = _cash

  def mode: Byte = _mode
}
