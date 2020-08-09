package net.server.channel.handlers;

import java.util.List;

import net.server.AbsoluteMovementData;
import net.server.AbstractPacketHandler;
import net.server.MaplePacket;
import net.server.MovementData;
import net.server.RelativeMovementData;
import server.maps.AnimatedMapleMapObject;

public abstract class AbstractMoveHandler<T extends MaplePacket> extends AbstractPacketHandler<T> {
   protected void processMovementList(List<MovementData> movementDataList, AnimatedMapleMapObject target) {
      for (MovementData movementData : movementDataList) {
         if (movementData instanceof AbsoluteMovementData) {
            target.setPosition(((AbsoluteMovementData) movementData).position());
            target.setStance(((AbsoluteMovementData) movementData).stance());
         } else if (movementData instanceof RelativeMovementData) {
            target.setStance(((RelativeMovementData) movementData).stance());
         }
      }
   }
}
