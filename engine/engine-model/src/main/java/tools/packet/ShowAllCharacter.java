package tools.packet;

import net.opcodes.SendOpcode;

public record ShowAllCharacter(Integer chars, Integer unk) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.VIEW_ALL_CHAR;
   }
}