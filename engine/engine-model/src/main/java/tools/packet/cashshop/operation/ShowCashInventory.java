package tools.packet.cashshop.operation;

import java.util.List;

import client.inventory.Item;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowCashInventory(Integer accountId, List<Item> items, Byte storageSlots,
                                Short characterSlots) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}