package tools.packet.cashshop.operation;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowWishList(List<Integer> sns, Boolean update) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}