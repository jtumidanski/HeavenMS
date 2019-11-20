package net.server.channel.packet.cash.use

class UseAvatarMegaphone(private var _position: Short, private var _itemId: Int, private var _messages: Array[String], private var _ear: Boolean) extends AbstractUseCashItemPacket(_position, _itemId) {
  def messages: Array[String] = _messages

  def ear: Boolean = _ear
}
