package net.server.task;

import net.server.channel.Channel;
import net.server.world.World;

public class MapOwnershipTask extends BaseTask implements Runnable {

   public MapOwnershipTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      for (Channel ch : world.getChannels()) {
         ch.runCheckOwnedMapsSchedule();
      }
   }
}
