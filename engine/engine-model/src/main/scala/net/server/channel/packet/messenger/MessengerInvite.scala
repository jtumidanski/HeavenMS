package net.server.channel.packet.messenger

class MessengerInvite(private var _mode: Byte, private var _input: String) extends BaseMessengerPacket(_mode = _mode) {
  def input: String = _input
}
