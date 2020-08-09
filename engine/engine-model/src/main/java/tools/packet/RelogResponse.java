package tools.packet;

import net.opcodes.SendOpcode;

public record RelogResponse() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.RELOG_RESPONSE;
   }
}