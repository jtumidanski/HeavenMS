package tools.packet.maker

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class MakerEnableActions() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.MAKER_RESULT
}