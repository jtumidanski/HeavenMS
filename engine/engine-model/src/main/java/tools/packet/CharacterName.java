package tools.packet;

import net.opcodes.SendOpcode;

public record CharacterName(String characterName, Boolean nameUsed) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CHAR_NAME_RESPONSE;
   }
}