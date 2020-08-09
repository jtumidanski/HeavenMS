package tools.packet.character.box;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemovePlayerShop(Integer characterId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.UPDATE_CHAR_BOX;
   }
}