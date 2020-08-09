package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SpouseMessage(String fiance, String text, Boolean spouse) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPOUSE_CHAT;
   }
}