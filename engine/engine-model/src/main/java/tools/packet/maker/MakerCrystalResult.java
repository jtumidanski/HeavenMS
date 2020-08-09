package tools.packet.maker;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MakerCrystalResult(Integer itemIdGained, Integer itemIdLost) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MAKER_RESULT;
   }
}