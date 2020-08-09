package tools.packet.maker;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public record MakerResult(boolean success, int itemMade, int itemCount, int mesos,
                          List<Pair<Integer, Integer>> itemsLost, int catalystId,
                          List<Integer> incBuffGems) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MAKER_RESULT;
   }
}