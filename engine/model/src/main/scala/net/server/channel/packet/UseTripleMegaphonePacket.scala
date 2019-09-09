package net.server.channel.packet

class UseTripleMegaphonePacket(private var _position: Short, private var _itemId: Int, private var _lines: Int, private var _message:Array[String], private var _whisper: Boolean) extends AbstractUseCashItemPacket(_position, _itemId) {
  def lines: Int = _lines

  def message: Array[String] = _message

  def whisper: Boolean = _whisper
}
