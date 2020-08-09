package server.movement;

import java.awt.Point;

public abstract class AbstractLifeMovement implements LifeMovement {
   private final int theType;

   private final Point position;

   private final int duration;

   private final int newState;

   public AbstractLifeMovement(int theType, Point position, int duration, int newState) {
      this.theType = theType;
      this.position = position;
      this.duration = duration;
      this.newState = newState;
   }

   public int theType() {
      return theType;
   }

   public Point position() {
      return position;
   }

   public int duration() {
      return duration;
   }

   public int newState() {
      return newState;
   }
}
