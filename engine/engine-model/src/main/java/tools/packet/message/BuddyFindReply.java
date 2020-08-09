package tools.packet.message;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record BuddyFindReply(String target, Integer mapId, Integer mapType) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.WHISPER;
   }
}