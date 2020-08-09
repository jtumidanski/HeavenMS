package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class JumpDownMovement extends AbstractLifeMovement {
   private final Point pixelsPerSecond;

   private final int fh;

   private final int originFh;

   public JumpDownMovement(int theType, Point position, int duration, int newState, Point pixelsPerSecond, int fh, int originFh) {
      super(theType, position, duration, newState);
      this.pixelsPerSecond = pixelsPerSecond;
      this.fh = fh;
      this.originFh = originFh;
   }

   public Point pixelsPerSecond() {
      return pixelsPerSecond;
   }

   public int fh() {
      return fh;
   }

   public int originFh() {
      return originFh;
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(theType());
      lew.writeShort(position().x);
      lew.writeShort(position().y);
      lew.writeShort(pixelsPerSecond().x);
      lew.writeShort(pixelsPerSecond().y);
      lew.writeShort(fh());
      lew.writeShort(originFh());
      lew.write(newState());
      lew.writeShort(duration());
   }
}
