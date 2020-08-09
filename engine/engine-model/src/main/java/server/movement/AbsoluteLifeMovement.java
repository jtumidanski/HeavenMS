package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class AbsoluteLifeMovement extends AbstractLifeMovement {
   private final Point pixelsPerSecond;

   private final int fh;

   public AbsoluteLifeMovement(int theType, Point position, int duration, int newState, Point pixelsPerSecond, int fh) {
      super(theType, position, duration, newState);
      this.pixelsPerSecond = pixelsPerSecond;
      this.fh = fh;
   }

   public Point pixelsPerSecond() {
      return pixelsPerSecond;
   }

   public int fh() {
      return fh;
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(theType());
      lew.writeShort(position().x);
      lew.writeShort(position().y);
      lew.writeShort(pixelsPerSecond().x);
      lew.writeShort(pixelsPerSecond().y);
      lew.writeShort(fh());
      lew.write(newState());
      lew.writeShort(duration());
   }
}
