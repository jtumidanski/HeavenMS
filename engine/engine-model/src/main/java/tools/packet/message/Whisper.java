package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record Whisper(String sender, Integer channel, String text) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WHISPER;
   }
}