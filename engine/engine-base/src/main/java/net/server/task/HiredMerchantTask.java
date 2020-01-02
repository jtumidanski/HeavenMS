package net.server.task;

import net.server.world.World;

public class HiredMerchantTask extends BaseTask implements Runnable {

   public HiredMerchantTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runHiredMerchantSchedule();
   }
}
