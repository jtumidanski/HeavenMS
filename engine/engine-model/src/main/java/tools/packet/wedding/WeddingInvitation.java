package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WeddingInvitation(String groom, String bride) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MARRIAGE_RESULT;
   }
}