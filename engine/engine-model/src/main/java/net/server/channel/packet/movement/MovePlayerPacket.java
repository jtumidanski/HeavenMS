package net.server.channel.packet.movement;

import java.util.List;

import net.server.MovementData;

public class MovePlayerPacket extends BaseMovementPacket {
   private final Boolean hasMovement;

   private final List<MovementData> movementDataList;

   private final List<Byte> movementList;

   public MovePlayerPacket(Boolean hasMovement, List<MovementData> movementDataList, List<Byte> movementList) {
      this.hasMovement = hasMovement;
      this.movementDataList = movementDataList;
      this.movementList = movementList;
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
