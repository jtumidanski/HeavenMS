package net.server.channel.packet.interaction

class OmokMovePlayerInteractionPacket(private var _mode: Byte, private var _x: Int, private var _y: Int, private var _type: Int) extends BasePlayerInteractionPacket(_mode = _mode) {
  def x: Int = _x

  def y: Int = _y

  def theType: Int = _type
}