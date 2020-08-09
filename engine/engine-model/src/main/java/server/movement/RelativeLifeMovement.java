package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class RelativeLifeMovement extends AbstractLifeMovement {
   public RelativeLifeMovement(int theType, Point position, int duration, int newState) {
      super(theType, position, duration, newState);
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(theType());
      lew.writeShort(position().x);
      lew.writeShort(position().y);
      lew.write(newState());
      lew.writeShort(duration());
   }
}
