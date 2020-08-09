package tools.packet.inventory;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SlotLimitUpdate(Integer inventoryType, Integer newLimit) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.INVENTORY_GROW;
   }
}