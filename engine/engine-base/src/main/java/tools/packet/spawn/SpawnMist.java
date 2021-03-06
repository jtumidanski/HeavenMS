package tools.packet.spawn;

import net.opcodes.SendOpcode;
import server.maps.MapleMist;
import tools.packet.PacketInput;

public class SpawnMist implements PacketInput {
   private final int objectId;

   private final int ownerId;

   private final int skillId;

   private final int level;

   private final MapleMist mist;

   public SpawnMist(int objectId, int ownerId, int skillId, int level, MapleMist mist) {
      this.objectId = objectId;
      this.ownerId = ownerId;
      this.skillId = skillId;
      this.level = level;
      this.mist = mist;
   }

   @Override
   public SendOpcode opcode() {
      return SendOpcode.SPAWN_MIST;
   }

   public int getObjectId() {
      return objectId;
   }

   public int getOwnerId() {
      return ownerId;
   }

   public int getSkillId() {
      return skillId;
   }

   public int getLevel() {
      return level;
   }

   public MapleMist getMist() {
      return mist;
   }
}
