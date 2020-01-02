package server.movement;

import java.awt.Point;

public interface LifeMovement extends LifeMovementFragment {
   Point position();

   int newState();

   int duration();

   int theType();
}
