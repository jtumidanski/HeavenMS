package tools.packet.cashshop.operation

import net.opcodes.SendOpcode
import tools.packet.PacketInput
import tools.packet.cashshop.CashShopMessage

class ShowCashShopMessage(private var _message: CashShopMessage) extends PacketInput {
  def message: CashShopMessage = _message

  override def opcode(): SendOpcode = SendOpcode.CASH_SHOP_OPERATION
}