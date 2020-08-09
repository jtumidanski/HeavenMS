package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowChair(Integer characterId, Integer itemId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_CHAIR;
   }
}