package tools.packet.pyramid;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PyramidScore(Byte score, Integer exp) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PYRAMID_SCORE;
   }
}