package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class Whisper(private var _sender: String, private var _channel: Int, private var _text: String) extends PacketInput {
  def sender: String = _sender

  def channel: Int = _channel

  def text: String = _text

  override def opcode(): SendOpcode = SendOpcode.WHISPER
}