package tools.packet.monster.book;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record ChangeCover(Integer cardId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_BOOK_SET_COVER;
   }
}