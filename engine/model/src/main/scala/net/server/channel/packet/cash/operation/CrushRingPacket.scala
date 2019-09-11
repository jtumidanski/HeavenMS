package net.server.channel.packet.cash.operation

class CrushRingPacket(private var _action: Int, private var _birthday: Int, private var _toCharge: Int, private var _sn: Int, private var _recipientName: String, private var _text: String) extends BaseCashOperationPacket(_action = _action) {
  def birthday: Int = _birthday

  def toCharge: Int = _toCharge

  def sn: Int = _sn

  def recipientName: String = _recipientName

  def text: String = _text
}
