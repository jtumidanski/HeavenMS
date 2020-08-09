package tools.packet.pq.ariant;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowAriantScoreboard() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ARIANT_ARENA_SHOW_RESULT;
   }
}