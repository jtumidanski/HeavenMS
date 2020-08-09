package tools.packet.character.box;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record UseChalkboard(Integer characterId, Boolean close, String text) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHALKBOARD;
   }
}