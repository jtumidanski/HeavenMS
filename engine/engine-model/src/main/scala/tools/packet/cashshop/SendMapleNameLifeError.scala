package tools.packet.cashshop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendMapleNameLifeError() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.MAPLE_LIFE_RESULT
}