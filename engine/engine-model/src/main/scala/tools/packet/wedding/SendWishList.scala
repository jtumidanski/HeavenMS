package tools.packet.wedding

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class SendWishList() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.MARRIAGE_REQUEST
}