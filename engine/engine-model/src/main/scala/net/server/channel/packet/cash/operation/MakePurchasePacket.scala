package net.server.channel.packet.cash.operation

class MakePurchasePacket(private var _action: Int, private var _useNX: Int, private var _snCS: Int) extends BaseCashOperationPacket(_action = _action) {
  def useNX: Int = _useNX

  def snCS: Int = _snCS
}
