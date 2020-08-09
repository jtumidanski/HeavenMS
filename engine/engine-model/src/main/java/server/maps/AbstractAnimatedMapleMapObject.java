package server.maps;

public abstract class AbstractAnimatedMapleMapObject extends AbstractMapleMapObject implements AnimatedMapleMapObject {
   private int stance = 0;

   @Override
   public boolean isFacingLeft() {
      return Math.abs(stance) % 2 == 1;
   }

   public int stance() {
      return stance;
   }

   public void setStance(int stance) {
      this.stance = stance;
   }
}
