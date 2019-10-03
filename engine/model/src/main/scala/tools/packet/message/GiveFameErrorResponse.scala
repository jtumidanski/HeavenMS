package tools.packet.message

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GiveFameErrorResponse(private var _status: Int) extends PacketInput {
  def status: Int = _status

  override def opcode(): SendOpcode = SendOpcode.FAME_RESPONSE
}