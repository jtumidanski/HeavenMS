package tools.packet.foreigneffect;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ShowForeignInfo(Integer characterId, String path) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SHOW_FOREIGN_EFFECT;
   }
}