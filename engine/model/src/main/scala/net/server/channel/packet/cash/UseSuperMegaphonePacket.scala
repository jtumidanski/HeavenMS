package net.server.channel.packet.cash

class UseSuperMegaphonePacket(private var _position: Short, private var _itemId: Int, private var _message:String, private var _ear:Boolean) extends AbstractUseCashItemPacket(_position, _itemId) {
  def message: String = _message

  def ear: Boolean = _ear
}
