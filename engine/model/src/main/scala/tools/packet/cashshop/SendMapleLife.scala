package tools.packet.cashshop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendMapleLife() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.MAPLELIFE_RESULT
}