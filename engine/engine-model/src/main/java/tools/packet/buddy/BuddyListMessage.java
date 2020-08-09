package tools.packet.buddy;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record BuddyListMessage(Byte message) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BUDDY_LIST;
   }
}
