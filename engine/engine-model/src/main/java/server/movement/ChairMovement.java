package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class ChairMovement extends AbstractLifeMovement {
   private final int fh;

   public ChairMovement(int theType, Point position, int duration, int newState, int fh) {
      super(theType, position, duration, newState);
      this.fh = fh;
   }

   public int fh() {
      return fh;
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(theType());
      lew.writeShort(position().x);
      lew.writeShort(position().y);
      lew.writeShort(fh);
      lew.write(newState());
      lew.writeShort(duration());
   }
}
