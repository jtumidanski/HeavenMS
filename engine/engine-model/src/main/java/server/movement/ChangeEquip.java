package server.movement;

import java.awt.Point;

import tools.data.output.LittleEndianWriter;

public class ChangeEquip implements LifeMovementFragment {
   private final int wui;

   public ChangeEquip(int wui) {
      this.wui = wui;
   }

   @Override
   public void serialize(LittleEndianWriter lew) {
      lew.write(10);
      lew.write(wui);
   }

   @Override
   public Point position() {
      return new Point(0, 0);
   }
}
