package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.life.MapleMonster;
import tools.packet.PacketInput;

public class SpawnFakeMonster implements PacketInput {
   private final MapleMonster monster;

   private final int effectId;

   public SpawnFakeMonster(MapleMonster monster, int effectId) {
      this.monster = monster;
      this.effectId = effectId;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MONSTER_CONTROL;
   }

   public MapleMonster getMonster() {
      return monster;
   }

   public int getEffectId() {
      return effectId;
   }
}
