package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class WhisperReply(private var _target: String, private var _reply: Byte) extends PacketInput {
  def target: String = _target

  def reply: Byte = _reply

  override def opcode(): SendOpcode = SendOpcode.WHISPER
}