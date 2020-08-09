package server.maps;

import java.awt.Point;

import client.MapleCharacter;
import client.SkillFactory;

public class MapleSummon extends AbstractAnimatedMapleMapObject {
   private MapleCharacter owner;
   private byte skillLevel;
   private int skillId, hp;
   private SummonMovementType movementType;

   public MapleSummon(MapleCharacter owner, int skillId, Point pos, SummonMovementType movementType) {
      this.owner = owner;
      this.skillId = skillId;

      SkillFactory.getSkill(skillId).ifPresent(skill -> {
         this.skillLevel = owner.getSkillLevel(skill);
         if (skillLevel == 0) {
            throw new RuntimeException();
         }

         this.movementType = movementType;
         setPosition(pos);
      });
   }

   public MapleCharacter getOwner() {
      return owner;
   }

   public int getSkill() {
      return skillId;
   }

   public int getHP() {
      return hp;
   }

   public void addHP(int delta) {
      this.hp += delta;
   }

   public SummonMovementType getMovementType() {
      return movementType;
   }

   public boolean isStationary() {
      return (skillId == 3111002 || skillId == 3211002 || skillId == 5211001 || skillId == 13111004);
   }

   public byte getSkillLevel() {
      return skillLevel;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.SUMMON;
   }

   public final boolean isPuppet() {
      return switch (skillId) {
         case 3111002, 3211002, 13111004 -> true;
         default -> false;
      };
   }
}
