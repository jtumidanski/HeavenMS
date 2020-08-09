package tools.packet;

import net.opcodes.SendOpcode;

public record EnableTV() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ENABLE_TV;
   }
}