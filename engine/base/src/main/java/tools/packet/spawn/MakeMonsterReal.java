package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleMonster;
import tools.packet.PacketInput;

public class MakeMonsterReal implements PacketInput {
   private MapleMonster monster;

   public MakeMonsterReal(MapleMonster monster) {
      this.monster = monster;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER;
   }

   public MapleMonster getMonster() {
      return monster;
   }
}
