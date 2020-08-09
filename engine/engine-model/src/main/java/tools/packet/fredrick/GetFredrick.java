package tools.packet.fredrick;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record GetFredrick(Byte operation) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.FREDRICK;
   }
}