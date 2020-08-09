package tools.packet.cashshop.operation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowGiftSucceed(String to, Integer itemId, Short count, Integer price) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}