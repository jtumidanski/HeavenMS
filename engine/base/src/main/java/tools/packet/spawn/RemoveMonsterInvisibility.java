package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleMonster;
import tools.packet.PacketInput;

public class RemoveMonsterInvisibility implements PacketInput {
   private MapleMonster monster;

   public RemoveMonsterInvisibility(MapleMonster monster) {
      this.monster = monster;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER_CONTROL;
   }

   public MapleMonster getMonster() {
      return monster;
   }
}
