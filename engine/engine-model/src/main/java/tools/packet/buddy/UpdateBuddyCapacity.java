package tools.packet.buddy;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UpdateBuddyCapacity(Integer capacity) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BUDDY_LIST;
   }
}
