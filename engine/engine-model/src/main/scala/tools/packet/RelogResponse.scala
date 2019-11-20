package tools.packet

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RelogResponse() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.RELOG_RESPONSE
}