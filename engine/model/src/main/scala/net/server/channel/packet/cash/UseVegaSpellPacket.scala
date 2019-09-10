package net.server.channel.packet.cash

class UseVegaSpellPacket(private var _position: Short, private var _itemId: Int, private var _firstCheck: Boolean, private var _equipSlot: Byte, private var _secondCheck: Boolean, private var _useSlot: Byte) extends AbstractUseCashItemPacket(_position, _itemId) {
  def firstCheck: Boolean = _firstCheck

  def equipSlot: Byte = _equipSlot

  def secondCheck: Boolean = _secondCheck

  def useSlot: Byte = _useSlot
}
