package tools.packet.wedding;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MarriageResultError(Byte message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MARRIAGE_RESULT;
   }
}