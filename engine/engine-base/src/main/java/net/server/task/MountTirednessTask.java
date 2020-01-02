package net.server.task;

import net.server.world.World;

public class MountTirednessTask extends BaseTask implements Runnable {

   public MountTirednessTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runMountSchedule();
   }
}
