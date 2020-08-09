package tools.packet.transfer.name;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record NameChangeError(Integer error) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_CHECK_NAME_CHANGE_POSSIBLE_RESULT;
   }
}