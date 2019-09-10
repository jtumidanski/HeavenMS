package net.server.channel.packet.guild

class ExpelFromGuildPacket(private var _type: Byte, private var _playerId: Int, private var _name: String) extends BaseGuildOperationPacket(_type = _type) {
  def playerId: Int = _playerId

  def name: String = _name
}
