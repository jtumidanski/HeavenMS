package net.server.channel.packet.cash.operation

class PutIntoCashInventoryPacket(private var _action: Int, private var _cashId: Int, private var _invType: Byte) extends BaseCashOperationPacket(_action = _action) {
  def cashId: Int = _cashId

  def invType: Byte = _invType
}
