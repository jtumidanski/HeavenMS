package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GiveFameResponse(Integer mode, String characterName, Integer newFame) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FAME_RESPONSE;
   }
}