package tools.packet.monster.book;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record SetCard(Boolean full, Integer cardId, Integer level) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.MONSTER_BOOK_SET_CARD;
   }
}