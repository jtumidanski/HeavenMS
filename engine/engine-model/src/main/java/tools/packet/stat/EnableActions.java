package tools.packet.stat;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record EnableActions() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STAT_CHANGED;
   }
}