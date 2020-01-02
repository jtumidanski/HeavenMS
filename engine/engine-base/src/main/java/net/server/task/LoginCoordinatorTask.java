package net.server.task;

import net.server.coordinator.session.MapleSessionCoordinator;

public class LoginCoordinatorTask implements Runnable {
   @Override
   public void run() {
      MapleSessionCoordinator.getInstance().runUpdateHwidHistory();
   }
}
