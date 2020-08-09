package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record WhisperReply(String target, Byte reply) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WHISPER;
   }
}