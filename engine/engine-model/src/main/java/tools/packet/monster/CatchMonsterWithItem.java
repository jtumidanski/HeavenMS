package tools.packet.monster;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record CatchMonsterWithItem(Integer objectId, Integer itemId, Byte success) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.CATCH_MONSTER_WITH_ITEM;
   }
}