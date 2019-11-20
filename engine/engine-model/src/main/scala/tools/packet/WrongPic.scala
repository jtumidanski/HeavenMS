package tools.packet

import net.opcodes.SendOpcode

class WrongPic() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.CHECK_SPW_RESULT
}