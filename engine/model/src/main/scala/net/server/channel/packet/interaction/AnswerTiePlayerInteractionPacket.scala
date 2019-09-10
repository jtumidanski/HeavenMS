package net.server.channel.packet.interaction

class AnswerTiePlayerInteractionPacket(private var _mode: Byte, private var _answer: Boolean) extends BasePlayerInteractionPacket(_mode = _mode) {
  def answer: Boolean = _answer
}