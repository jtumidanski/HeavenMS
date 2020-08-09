package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetClock(Integer time) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CLOCK;
   }
}