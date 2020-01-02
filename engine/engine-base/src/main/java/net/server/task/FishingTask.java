package net.server.task;

import net.server.world.World;

public class FishingTask extends BaseTask implements Runnable {

   public FishingTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runCheckFishingSchedule();
   }
}
