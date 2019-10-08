package tools.packet.ui

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DisableMiniMap() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.ADMIN_RESULT
}