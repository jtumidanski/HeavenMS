package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleMonster;
import tools.packet.PacketInput;

public class ControlMonster implements PacketInput {
   private MapleMonster monster;

   private boolean newSpawn;

   private boolean aggro;

   public ControlMonster(MapleMonster monster, boolean newSpawn, boolean aggro) {
      this.monster = monster;
      this.newSpawn = newSpawn;
      this.aggro = aggro;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER_CONTROL;
   }

   public MapleMonster getMonster() {
      return monster;
   }

   public boolean isNewSpawn() {
      return newSpawn;
   }

   public boolean isAggro() {
      return aggro;
   }
}
