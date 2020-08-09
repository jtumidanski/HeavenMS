package tools.packet;

import net.opcodes.SendOpcode;

public record DeleteCharacter(Integer characterId, DeleteCharacterResponse state) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.DELETE_CHAR_RESPONSE;
   }
}