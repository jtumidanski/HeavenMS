package tools.packet.report

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ReportResponse(private var _mode: Byte) extends PacketInput {
  def mode: Byte = _mode

  override def opcode(): SendOpcode = SendOpcode.SUE_CHARACTER_RESULT
}