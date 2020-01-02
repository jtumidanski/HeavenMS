package net.server.task;

import net.server.world.World;

public class PartySearchTask extends BaseTask implements Runnable {

   public PartySearchTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runPartySearchUpdateSchedule();
   }
}
