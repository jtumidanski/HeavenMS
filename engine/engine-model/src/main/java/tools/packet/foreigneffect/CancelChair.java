package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CancelChair(Integer itemId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CANCEL_CHAIR;
   }
}