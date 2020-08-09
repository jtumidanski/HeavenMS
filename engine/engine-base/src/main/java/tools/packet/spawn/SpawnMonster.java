package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleMonster;
import tools.packet.PacketInput;

public class SpawnMonster implements PacketInput {
   private final MapleMonster monster;

   private final boolean newSpawn;

   private final int effect;

   public SpawnMonster(MapleMonster monster, boolean newSpawn, int effect) {
      this.monster = monster;
      this.newSpawn = newSpawn;
      this.effect = effect;
   }

   public SpawnMonster(MapleMonster monster, boolean newSpawn) {
      this(monster, newSpawn, 0);
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER;
   }

   public MapleMonster getMonster() {
      return monster;
   }

   public boolean isNewSpawn() {
      return newSpawn;
   }

   public int getEffect() {
      return effect;
   }
}
