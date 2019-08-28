package tools;

import java.awt.Point;

public class PointUtil {
   /**
    * Fetches angle relative between spawn and door points where 3 O'Clock is 0
    * and 12 O'Clock is 270 degrees
    *
    * @param spawnPoint
    * @param doorPoint
    * @return angle in degress from 0-360.
    */
   public static double getAngle(Point doorPoint, Point spawnPoint) {
      double dx = doorPoint.getX() - spawnPoint.getX();
      // Minus to correct for coord re-mapping
      double dy = -(doorPoint.getY() - spawnPoint.getY());

      double inRads = Math.atan2(dy, dx);

      // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
      if (inRads < 0) {
         inRads = Math.abs(inRads);
      } else {
         inRads = 2 * Math.PI - inRads;
      }

      return Math.toDegrees(inRads);
   }

   /**
    * Converts angle in degrees to rounded cardinal coordinate.
    *
    * @param angle
    * @return correspondent coordinate.
    */
   public static String getRoundedCoordinate(double angle) {
      String[] directions = {"E", "SE", "S", "SW", "W", "NW", "N", "NE", "E"};
      return directions[(int) Math.round(((angle % 360) / 45))];
   }
}
