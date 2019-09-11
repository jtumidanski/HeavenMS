package net.server.channel.packet.cash.use

class UseMapleTvPacket(private var _position: Short, private var _itemId: Int, private var _megaMessenger: Boolean, private var _ear: Boolean, private var _characterName: String, private var _messages: Array[String]) extends AbstractUseCashItemPacket(_position, _itemId) {
  def megaMessenger: Boolean = _megaMessenger

  def ear: Boolean = _ear

  def characterName: String = _characterName

  def messages: Array[String] = _messages
}
