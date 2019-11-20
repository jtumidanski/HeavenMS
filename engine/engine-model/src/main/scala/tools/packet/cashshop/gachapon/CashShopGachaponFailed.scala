package tools.packet.cashshop.gachapon

import net.opcodes.SendOpcode
import tools.packet.PacketInput

class CashShopGachaponFailed() extends PacketInput {
  override def opcode(): SendOpcode = SendOpcode.CASHSHOP_CASH_ITEM_GACHAPON_RESULT
}