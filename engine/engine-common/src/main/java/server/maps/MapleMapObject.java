package server.maps;

import java.awt.Point;

public interface MapleMapObject {
   int objectId();

   void setObjectId(int id);

   MapleMapObjectType type();

   Point position();

   void setPosition(Point position);

   void nullifyPosition();
}