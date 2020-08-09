package tools.packet.messenger;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MessengerNote(String text, Integer mode, Integer mode2) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MESSENGER;
   }
}