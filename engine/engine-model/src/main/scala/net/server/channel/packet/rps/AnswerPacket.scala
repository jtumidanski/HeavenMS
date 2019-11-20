package net.server.channel.packet.rps

class AnswerPacket(private var _available: Boolean, private var _mode: Byte, private var _answer: Byte) extends BaseRPSActionPacket(_available = _available, _mode = _mode) {
  def answer: Byte = _answer
}
