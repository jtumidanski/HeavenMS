package net.server.task;

import net.server.world.World;

public class TimedMapObjectTask extends BaseTask implements Runnable {

   public TimedMapObjectTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runTimedMapObjectSchedule();
   }
}
