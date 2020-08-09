package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveSummon(Integer ownerId, Integer objectId, Boolean animated) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_SPECIAL_MAP_OBJECT;
   }
}