package tools.packet.remove;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveItem(Integer objectId, Integer animation, Integer characterId, Boolean pet,
                         Integer slot) implements PacketInput {
   public RemoveItem(Integer objectId, Integer animation, Integer characterId) {
      this(objectId, animation, characterId, false, 0);
   }

   public RemoveItem(Integer objectId) {
      this(objectId, 1, 0);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.REMOVE_ITEM_FROM_MAP;
   }
}