package net.server.channel.packet.cash.operation

class SendGiftPacket(private var _action: Int, private var _birthday: Int, private var _sn: Int, private var _characterName: String, private var _message: String) extends BaseCashOperationPacket(_action = _action) {
  def birthday: Int = _birthday

  def sn: Int = _sn

  def characterName: String = _characterName

  def message: String = _message
}
