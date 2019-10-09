package tools.packet.spawn

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CannotSpawnKite() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.CANNOT_SPAWN_KITE
}