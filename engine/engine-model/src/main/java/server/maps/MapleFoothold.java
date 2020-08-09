package server.maps;

import java.awt.Point;

public record MapleFoothold(Point firstPoint, Point secondPoint, Integer id) implements Comparable<MapleFoothold> {
   public Boolean isWall() {
      return firstPoint.getX() == secondPoint.getX();
   }

   @Override
   public int compareTo(MapleFoothold o) {
      if (secondPoint.getY() < o.firstPoint().getY()) {
         return -1;
      } else if (firstPoint.getY() > o.secondPoint().getY()) {
         return 1;
      }
      return 0;
   }
}
