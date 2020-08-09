package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ReceiveFame(Integer mode, String characterNameFrom) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAME_RESPONSE;
   }
}