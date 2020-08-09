package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ChatText(Integer characterIdFrom, String text, Boolean gm, Integer show) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHAT_TEXT;
   }
}