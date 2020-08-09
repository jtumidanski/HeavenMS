package tools.packet.cashshop;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SendMapleNameLifeError() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MAPLE_LIFE_RESULT;
   }
}