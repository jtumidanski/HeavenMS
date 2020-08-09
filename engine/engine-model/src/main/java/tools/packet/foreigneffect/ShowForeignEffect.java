package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowForeignEffect(Integer characterId, Integer effect) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_FOREIGN_EFFECT;
   }
}