package tools.packet.cashshop.operation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBoughtQuestItem(Integer itemId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}