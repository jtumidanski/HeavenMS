package tools.packet.cashshop.operation;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.Gift;
import tools.packet.PacketInput;

public record ShowGifts(List<Gift> gifts) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}