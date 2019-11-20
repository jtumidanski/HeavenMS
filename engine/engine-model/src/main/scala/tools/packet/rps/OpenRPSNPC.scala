package tools.packet.rps

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class OpenRPSNPC() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.RPS_GAME
}