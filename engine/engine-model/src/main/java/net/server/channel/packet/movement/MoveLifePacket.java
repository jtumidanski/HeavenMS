package net.server.channel.packet.movement;

import java.util.List;

import net.server.MovementData;

public class MoveLifePacket extends BaseMovementPacket {
   private final Integer objectId;

   private final Short moveId;

   private final Byte pNibbles;

   private final Byte rawActivity;

   private final Integer skillId;

   private final Integer skillLevel;

   private final Short pOption;

   private final Short startX;

   private final Short startY;

   private final Boolean hasMovement;

   private final List<MovementData> movementDataList;

   private final List<Byte> movementList;

   public MoveLifePacket(Integer objectId, Short moveId, Byte pNibbles, Byte rawActivity, Integer skillId,
                         Integer skillLevel, Short pOption, Short startX, Short startY, Boolean hasMovement,
                         List<MovementData> movementDataList, List<Byte> movementList) {
      this.objectId = objectId;
      this.moveId = moveId;
      this.pNibbles = pNibbles;
      this.rawActivity = rawActivity;
      this.skillId = skillId;
      this.skillLevel = skillLevel;
      this.pOption = pOption;
      this.startX = startX;
      this.startY = startY;
      this.hasMovement = hasMovement;
      this.movementDataList = movementDataList;
      this.movementList = movementList;
   }

   public Integer objectId() {
      return objectId;
   }

   public Short moveId() {
      return moveId;
   }

   public Byte pNibbles() {
      return pNibbles;
   }

   public Byte rawActivity() {
      return rawActivity;
   }

   public Integer skillId() {
      return skillId;
   }

   public Integer skillLevel() {
      return skillLevel;
   }

   public Short pOption() {
      return pOption;
   }

   public Short startX() {
      return startX;
   }

   public Short startY() {
      return startY;
   }

   public Boolean hasMovement() {
      return hasMovement;
   }

   public List<MovementData> movementDataList() {
      return movementDataList;
   }

   public List<Byte> movementList() {
      return movementList;
   }
}
