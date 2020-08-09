package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MultiMegaphone(String[] messages, Integer channel, Boolean showEar) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SERVER_MESSAGE;
   }
}