package tools.packet.messenger;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MessengerRemoveCharacter(Integer position) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MESSENGER;
   }
}