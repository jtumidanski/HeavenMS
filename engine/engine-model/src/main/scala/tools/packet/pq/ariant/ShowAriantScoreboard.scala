package tools.packet.pq.ariant

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowAriantScoreboard() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.ARIANT_ARENA_SHOW_RESULT
}