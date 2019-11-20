package net.server.channel.packet.interaction

class CreateOmokPlayerInteractionPacket(private var _mode: Byte, private var _createType: Byte, private var _description: String, private var _hasPassword: Boolean, private var _password: String, private var _type: Int) extends BaseCreatePlayerInteractionPacket(_mode: Byte, _createType: Byte) {
  def description: String = _description

  def hasPassword: Boolean = _hasPassword

  def password: String = _password

  def theType: Int = _type
}
