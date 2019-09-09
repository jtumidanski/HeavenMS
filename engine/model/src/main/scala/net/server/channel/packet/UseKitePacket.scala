package net.server.channel.packet

class UseKitePacket(private var _position: Short, private var _itemId: Int, private var _message:String) extends AbstractUseCashItemPacket(_position, _itemId) {
  def message: String = _message
}
