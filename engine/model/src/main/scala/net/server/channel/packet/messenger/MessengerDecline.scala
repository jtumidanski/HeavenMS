package net.server.channel.packet.messenger

class MessengerDecline(private var _mode: Byte, private var _target: String) extends BaseMessengerPacket(_mode = _mode) {
  def target: String = _target
}
