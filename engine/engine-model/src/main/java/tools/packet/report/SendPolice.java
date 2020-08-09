package tools.packet.report;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SendPolice(String text) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DATA_CRC_CHECK_FAILED;
   }
}