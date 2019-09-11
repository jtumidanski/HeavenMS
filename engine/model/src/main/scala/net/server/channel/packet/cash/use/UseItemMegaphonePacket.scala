package net.server.channel.packet.cash.use

class UseItemMegaphonePacket(private var _position: Short, private var _itemId: Int, private var _message: String, private var _whisper: Boolean, private var _selected: Boolean, private var _inventoryType: Byte, private var _slot: Short) extends AbstractUseCashItemPacket(_position, _itemId) {
  def message: String = _message

  def whisper: Boolean = _whisper

  def selected: Boolean = _selected

  def inventoryType: Byte = _inventoryType

  def slot: Short = _slot
}
