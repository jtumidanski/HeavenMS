package tools.packet.foreigneffect;

import client.inventory.ScrollResult;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowScrollEffect(Integer characterId, ScrollResult success, Boolean legendarySpirit,
                               Boolean whiteScroll) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_SCROLL_EFFECT;
   }
}