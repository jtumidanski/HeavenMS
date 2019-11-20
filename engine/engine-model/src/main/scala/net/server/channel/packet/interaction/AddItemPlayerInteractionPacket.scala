package net.server.channel.packet.interaction

class AddItemPlayerInteractionPacket(private var _mode: Byte, private var _slotType: Byte, private var _slot: Short, private var _bundles: Short, private var _perBundle: Short, private var _price: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def slotType: Byte = _slotType

  def slot: Short = _slot

  def bundles: Short = _bundles

  def perBundle: Short = _perBundle

  def price: Int = _price
}