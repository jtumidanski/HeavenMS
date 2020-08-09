package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowForeignBuybackEffect(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_FOREIGN_EFFECT;
   }
}