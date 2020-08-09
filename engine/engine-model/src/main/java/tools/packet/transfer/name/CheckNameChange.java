package tools.packet.transfer.name;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CheckNameChange(String availableName, Boolean canUseName) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_CHECK_NAME_CHANGE;
   }
}