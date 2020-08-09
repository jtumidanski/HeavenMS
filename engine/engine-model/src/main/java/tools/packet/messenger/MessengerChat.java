package tools.packet.messenger;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MessengerChat(String text) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MESSENGER;
   }
}