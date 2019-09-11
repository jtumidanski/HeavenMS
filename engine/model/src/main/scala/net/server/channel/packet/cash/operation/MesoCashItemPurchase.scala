package net.server.channel.packet.cash.operation

class MesoCashItemPurchase(private var _action: Int, private var _sn: Int) extends BaseCashOperationPacket(_action = _action) {
  def sn: Int = _sn
}
