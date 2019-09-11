package net.server.channel.packet.cash.operation

class FriendshipRingPacket(private var _action: Int, private var _birthday: Int, private var _payment: Int, private var _sn: Int, private var _sentTo: String, private var _text: String) extends BaseCashOperationPacket(_action = _action) {
  def birthday: Int = _birthday

  def payment: Int = _payment

  def sn: Int = _sn

  def sentTo: String = _sentTo

  def text: String = _text
}
