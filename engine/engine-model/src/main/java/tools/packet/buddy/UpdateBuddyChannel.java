package tools.packet.buddy;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateBuddyChannel(int characterId, int channel) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BUDDY_LIST;
   }
}
