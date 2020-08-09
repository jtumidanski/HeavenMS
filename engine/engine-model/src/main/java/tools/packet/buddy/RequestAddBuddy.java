package tools.packet.buddy;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RequestAddBuddy(Integer characterIdFrom, Integer characterIdTo,
                              String characterNameFrom) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.BUDDY_LIST;
   }
}
