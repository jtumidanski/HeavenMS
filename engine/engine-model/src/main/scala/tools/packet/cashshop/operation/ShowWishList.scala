package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ShowWishList(private var _sns: java.util.List[Integer], private var _update: Boolean) extends PacketInput {
  def sns: java.util.List[Integer] = _sns

  def update: Boolean = _update

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}