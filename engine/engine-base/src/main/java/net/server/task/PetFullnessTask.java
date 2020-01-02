package net.server.task;

import net.server.world.World;

public class PetFullnessTask extends BaseTask implements Runnable {

   public PetFullnessTask(World world) {
      super(world);
   }

   @Override
   public void run() {
      world.runPetSchedule();
   }
}
