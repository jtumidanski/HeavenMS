package server.maps;

import java.awt.Point;

public interface MapleMapObject {
   int objectId();

   void objectId_$eq(int id);

   MapleMapObjectType type();

   Point position();

   void position_$eq(Point position);

   void nullifyPosition();
}