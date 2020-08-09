package tools.packet.ui;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetClockTime(Integer hour, Integer minute, Integer second) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CLOCK;
   }
}