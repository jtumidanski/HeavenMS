package tools.packet.cashshop.gachapon;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CashShopGachaponSuccess(int accountId, long sn, int remainingBoxes, Item item, int itemId,
                                      int selectedItemCount, boolean jackpot) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_CASH_ITEM_GACHAPON_RESULT;
   }
}