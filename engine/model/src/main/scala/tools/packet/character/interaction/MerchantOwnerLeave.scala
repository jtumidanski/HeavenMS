package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MerchantOwnerLeave() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}