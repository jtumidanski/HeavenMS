package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record FindReply(String target, int mapId, int mapType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WHISPER;
   }
}