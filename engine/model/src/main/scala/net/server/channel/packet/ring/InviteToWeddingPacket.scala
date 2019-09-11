package net.server.channel.packet.ring

class InviteToWeddingPacket(private var _mode: Byte, private var _name: String, private var _marriageId: Int, private var _slot: Byte) extends BaseRingPacket(_mode = _mode) {
  def name: String = _name

  def marriageId: Int = _marriageId

  def slot: Byte = _slot
}
