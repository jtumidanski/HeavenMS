package net.server.task;

import net.server.world.World;

public abstract class BaseTask implements Runnable {
   protected World world;

   public BaseTask(World world) {
      this.world = world;
   }

   @Override
   public void run() {
   }
}
