package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MarriageResultError(private var _message: Byte) extends PacketInput {
  def message: Byte = _message

  override def opcode(): SendOpcode = SendOpcode.MARRIAGE_RESULT
}