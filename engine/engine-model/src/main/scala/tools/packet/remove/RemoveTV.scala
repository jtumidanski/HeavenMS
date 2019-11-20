package tools.packet.remove

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class RemoveTV() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.REMOVE_TV
}