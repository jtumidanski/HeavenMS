package tools.packet.family

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class LoadFamily() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.FAMILY_PRIVILEGE_LIST
}