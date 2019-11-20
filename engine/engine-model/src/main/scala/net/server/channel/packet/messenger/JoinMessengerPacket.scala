package net.server.channel.packet.messenger

class JoinMessengerPacket(private var _mode: Byte, private var _messengerId: Int) extends BaseMessengerPacket(_mode = _mode) {
  def messengerId: Int = _messengerId
}
