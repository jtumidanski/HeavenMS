package net.server.task;

import net.server.coordinator.world.MapleInviteCoordinator;

public class InvitationTask implements Runnable {

   @Override
   public void run() {
      MapleInviteCoordinator.runTimeoutSchedule();
   }
}
