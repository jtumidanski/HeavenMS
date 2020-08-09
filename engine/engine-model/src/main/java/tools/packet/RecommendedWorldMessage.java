package tools.packet;

import java.util.List;

import net.opcodes.SendOpcode;
import server.WorldRecommendation;

public record RecommendedWorldMessage(List<WorldRecommendation> worlds) implements PacketInput {
  @Override
  public SendOpcode opcode() {
    return SendOpcode.RECOMMENDED_WORLD_MESSAGE;
  }
}