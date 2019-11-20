package tools.packet.character

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CharacterKnockBack() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.LEFT_KNOCK_BACK
}