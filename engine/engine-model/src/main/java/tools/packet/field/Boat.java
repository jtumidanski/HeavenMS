package tools.packet.field;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record Boat(Boolean theType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CONTI_STATE;
   }
}