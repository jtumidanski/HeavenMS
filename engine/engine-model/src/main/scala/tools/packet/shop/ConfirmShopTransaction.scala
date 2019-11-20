package tools.packet.shop

import constants.ShopTransactionOperation
import net.opcodes.SendOpcode
import tools.packet.PacketInput

class ConfirmShopTransaction(private var _operation: ShopTransactionOperation) extends PacketInput {
  def operation: ShopTransactionOperation = _operation

  override def opcode(): SendOpcode = SendOpcode.CONFIRM_SHOP_TRANSACTION
}