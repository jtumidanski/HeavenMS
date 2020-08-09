package net.server.channel.packet.movement;

import java.awt.Point;
import java.util.List;

import net.server.MovementData;

public class MoveSummonPacket extends BaseMovementPacket {
   private final Integer objectId;

   private final Point startPosition;

   private final Boolean hasMovement;

   private final List<MovementData> movementDataList;

   private final List<Byte> movementList;

   public MoveSummonPacket(Integer objectId, Point startPosition, Boolean hasMovement, List<MovementData> movementDataList, List<Byte> movementList) {
      this.objectId = objectId;
      this.startPosition = startPosition;
      this.hasMovement = hasMovement;
      this.movementDataList = movementDataList;
      this.movementList = movementList;
   }

   public Integer objectId() {
      return objectId;
   }

   public Point startPosition() {
      return startPosition;
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
