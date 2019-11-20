package net.server.channel.packet.interaction

class VisitPlayerInteractionPacket(private var _mode: Byte, private var _objectId: Int, private var _password: String) extends BasePlayerInteractionPacket(_mode = _mode) {
  def objectId: Int = _objectId

  def password: String = _password

  def this(mode: Byte, objectId: Int) {
    this(mode, objectId, "")
  }
}
