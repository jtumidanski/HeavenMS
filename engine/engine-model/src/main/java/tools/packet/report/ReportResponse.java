package tools.packet.report;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ReportResponse(Byte mode) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SUE_CHARACTER_RESULT;
   }
}