package tools.packet.character.interaction

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MerchantMaintenanceMessage() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.PLAYER_INTERACTION
}