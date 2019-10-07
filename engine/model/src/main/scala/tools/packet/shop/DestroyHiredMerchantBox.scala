package tools.packet.shop

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class DestroyHiredMerchantBox(private var _ownerId: Int) extends PacketInput {
  def ownerId: Int = _ownerId

  override def opcode(): SendOpcode = SendOpcode.DESTROY_HIRED_MERCHANT
}