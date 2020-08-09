package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record MultiChat(String name, String text, Integer mode) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MULTI_CHAT;
   }
}