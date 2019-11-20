package net.server.channel.packet.ring

class HandleWishListPacket(private var _mode: Byte, private var _amount: Int, private var _items: Array[String]) extends BaseRingPacket(_mode = _mode) {
  def amount: Int = _amount

  def items: Array[String] = _items
}
