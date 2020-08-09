package tools.packet.pyramid;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record PyramidGauge(Integer gauge) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.PYRAMID_GAUGE;
   }
}