package tools.packet.shop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowHiredMerchantBox() extends PacketInput {

  override def opcode(): SendOpcode = SendOpcode.ENTRUSTED_SHOP_CHECK_RESULT
}