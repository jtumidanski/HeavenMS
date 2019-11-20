package net.server.channel.packet.messenger

class MessengerChat(private var _mode: Byte, private var _input: String) extends BaseMessengerPacket(_mode = _mode) {
  def input: String = _input
}
