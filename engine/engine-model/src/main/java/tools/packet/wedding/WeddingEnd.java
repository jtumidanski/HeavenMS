package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WeddingEnd(Boolean blessEffect, Integer groomId, Integer brideId, Byte step) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WEDDING_CEREMONY_END;
   }
}