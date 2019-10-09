package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ForcedStatReset() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.FORCED_STAT_RESET
}