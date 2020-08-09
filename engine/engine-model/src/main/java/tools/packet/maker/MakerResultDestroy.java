package tools.packet.maker;

import java.util.List;

import net.opcodes.SendOpcode;
import tools.Pair;
import tools.packet.PacketInput;

public record MakerResultDestroy(Integer itemId, Integer mesos,
                                 List<Pair<Integer, Integer>> itemsGained) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MAKER_RESULT;
   }
}