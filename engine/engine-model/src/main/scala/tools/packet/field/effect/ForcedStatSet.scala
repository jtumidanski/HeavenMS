package tools.packet.field.effect

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ForcedStatSet() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.FORCED_STAT_SET
}