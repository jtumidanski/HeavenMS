package tools.packet;

import net.opcodes.SendOpcode;

public record Ping() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PING;
   }
}