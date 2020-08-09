package server.maps;

import java.awt.Point;

public abstract class AbstractMapleMapObject implements MapleMapObject {
   private Point position;

   private Integer objectId;

   @Override
   public int objectId() {
      return objectId;
   }

   @Override
   public void setObjectId(int id) {
      objectId = id;
   }

   @Override
   public Point position() {
      return new Point(position);
   }

   @Override
   public void setPosition(Point position) {
      if (this.position != null) {
         this.position.move(position.x, position.y);
      }
   }

   @Override
   public void nullifyPosition() {
      position = null;
   }
}
