package tools.packet.stat

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class EnableActions() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.STAT_CHANGED
}