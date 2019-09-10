package net.server.channel.packet.interaction

class BuyPlayerInteractionPacket(private var _mode: Byte, private var _itemId: Int, private var _quantity: Short) extends BasePlayerInteractionPacket(_mode = _mode) {
  def itemId: Int = _itemId

  def quantity: Short = _quantity
}