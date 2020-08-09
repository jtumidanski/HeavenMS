package tools.packet.pq.ariant;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateAriantRanking(List<AriantScore> scores) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.ARIANT_ARENA_USER_SCORE;
   }
}