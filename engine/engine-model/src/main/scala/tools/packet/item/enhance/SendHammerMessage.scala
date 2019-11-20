package tools.packet.item.enhance

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendHammerMessage() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.VICIOUS_HAMMER
}