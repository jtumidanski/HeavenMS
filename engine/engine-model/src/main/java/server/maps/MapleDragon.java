package server.maps;

import java.awt.Point;

public class MapleDragon extends AbstractAnimatedMapleMapObject {
   private final Integer ownerId;

   public MapleDragon(Integer ownerId, Point position, Integer stance) {
      this.ownerId = ownerId;
      setObjectId(ownerId);
      setPosition(position);
      setStance(stance);
   }

   public Integer ownerId() {
      return ownerId;
   }

   @Override
   public MapleMapObjectType type() {
      return MapleMapObjectType.DRAGON;
   }
}
