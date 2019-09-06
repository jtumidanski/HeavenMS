package net.server.channel.packet

class UseNotePacket(private var _position: Short, private var _itemId: Int, private var _to: String, private var _message: String) extends AbstractUseCashItemPacket(_position, _itemId) {
  def to: String = _to

  def message: String = _message
}
