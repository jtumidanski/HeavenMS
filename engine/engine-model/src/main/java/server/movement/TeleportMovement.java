package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class TeleportMovement extends AbsoluteLifeMovement {
   public TeleportMovement(int theType, Point position, int newState, Point pixelsPerSecond) {
      super(theType, position, 0, newState, pixelsPerSecond, 0);
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(theType());
      lew.writeShort(position().x);
      lew.writeShort(position().y);
      lew.writeShort(pixelsPerSecond().x);
      lew.writeShort(pixelsPerSecond().y);
      lew.write(newState());
   }
}
