package tools.packet.cashshop.operation;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RefundCashItem(Item item, Integer maplePoints) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}