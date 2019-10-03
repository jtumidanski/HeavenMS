package tools.packet

import net.opcodes.SendOpcode

class EnableTV() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.ENABLE_TV
}