package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record StopClock() implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.STOP_CLOCK;
   }
}