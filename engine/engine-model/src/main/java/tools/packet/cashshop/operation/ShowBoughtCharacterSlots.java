package tools.packet.cashshop.operation;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowBoughtCharacterSlots(Short slots) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CASH_SHOP_OPERATION;
   }
}