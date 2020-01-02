package net.server.task;

import net.server.coordinator.world.MapleEventRecallCoordinator;

public class EventRecallCoordinatorTask implements Runnable {
   @Override
   public void run() {
      MapleEventRecallCoordinator.getInstance().manageEventInstances();
   }
}
