package net.server.channel.packet.cash.operation

class ModifyWishListPacket(private var _action: Int, private var _sns: Array[Int]) extends BaseCashOperationPacket(_action = _action) {
  def sns: Array[Int] = _sns
}
