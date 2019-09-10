package net.server.channel.packet.interaction

class SetItemsPlayerInteractionPacket(private var _mode: Byte, private var _slotType: Byte, private var _position: Short, private var _quantity: Short, private var _targetSlot: Byte) extends BasePlayerInteractionPacket(_mode = _mode) {
  def slotType: Byte = _slotType

  def position: Short = _position

  def quantity: Short = _quantity

  def targetSlot: Byte = _targetSlot
}