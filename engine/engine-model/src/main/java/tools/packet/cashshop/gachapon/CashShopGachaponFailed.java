package tools.packet.cashshop.gachapon;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CashShopGachaponFailed() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_CASH_ITEM_GACHAPON_RESULT;
   }
}