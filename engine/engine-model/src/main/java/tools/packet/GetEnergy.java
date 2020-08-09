package tools.packet;

import net.opcodes.SendOpcode;

public record GetEnergy(String info, int amount) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SESSION_VALUE;
   }
}