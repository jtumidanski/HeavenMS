package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowPetLevelUp(Integer characterId, Byte index) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_FOREIGN_EFFECT;
   }
}