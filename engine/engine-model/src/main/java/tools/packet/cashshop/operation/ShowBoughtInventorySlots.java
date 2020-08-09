package tools.packet.cashshop.operation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBoughtInventorySlots(int inventoryType, short slots) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}