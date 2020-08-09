package tools.packet.messenger;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MessengerJoin(Integer position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MESSENGER;
   }
}