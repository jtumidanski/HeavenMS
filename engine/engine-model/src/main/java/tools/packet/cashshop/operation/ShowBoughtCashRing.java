package tools.packet.cashshop.operation;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBoughtCashRing(Item ring, String recipient, Integer accountId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}