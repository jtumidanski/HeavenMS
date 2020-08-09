package tools.packet.spawn;

import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public record RemoveMonsterInvisibility(Integer monsterObjectId) implements PacketInput {
   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER_CONTROL;
   }
}