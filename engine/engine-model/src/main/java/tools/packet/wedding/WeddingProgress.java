package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WeddingProgress(Boolean blessEffect, Integer groomId, Integer brideId, Byte step) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WEDDING_PROGRESS;
   }
}