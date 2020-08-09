package tools.packet.cashshop.operation;

import java.util.List;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBoughtCashPackageSuccess(List<Item> cashPacket, Integer accountId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}