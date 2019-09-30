package tools.packet.playerinteraction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetMiniGameRequestTie() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}