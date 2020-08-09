package tools.packet;

import net.opcodes.SendOpcode;

public record EnableReport() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CLAIM_STATUS_CHANGED;
   }
}