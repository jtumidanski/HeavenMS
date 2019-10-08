package tools.packet.monster;

import java.util.List;

import client.status.MonsterStatusEffect;
import net.opcodes.SendOpcode;
import tools.packet.PacketInput;

public class ApplyMonsterStatus implements PacketInput {
   private int objectId;

   private MonsterStatusEffect statusEffect;

   private List<Integer> reflection;

   public ApplyMonsterStatus(int objectId, MonsterStatusEffect statusEffect, List<Integer> reflection) {
      this.objectId = objectId;
      this.statusEffect = statusEffect;
      this.reflection = reflection;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.APPLY_MONSTER_STATUS;
   }

   public int getObjectId() {
      return objectId;
   }

   public MonsterStatusEffect getStatusEffect() {
      return statusEffect;
   }

   public List<Integer> getReflection() {
      return reflection;
   }
}
