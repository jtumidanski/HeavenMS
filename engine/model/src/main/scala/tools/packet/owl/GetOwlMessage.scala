package tools.packet.owl

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class GetOwlMessage(private var _message: Int) extends PacketInput {
  def message: Int = _message

  override def opcode(): SendOpcode = SendOpcode.SHOP_LINK_RESULT
}