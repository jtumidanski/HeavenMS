package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GiveFameErrorResponse(Integer status) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAME_RESPONSE;
   }
}