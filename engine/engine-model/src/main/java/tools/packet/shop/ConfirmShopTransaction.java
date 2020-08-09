package tools.packet.shop;

import constants.ShopTransactionOperation;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ConfirmShopTransaction(ShopTransactionOperation operation) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CONFIRM_SHOP_TRANSACTION;
   }
}