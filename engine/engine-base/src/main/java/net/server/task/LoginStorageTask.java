package net.server.task;

import net.server.coordinator.login.MapleLoginBypassCoordinator;
import net.server.coordinator.session.MapleSessionCoordinator;

public class LoginStorageTask implements Runnable {
   @Override
   public void run() {
      MapleSessionCoordinator.getInstance().runUpdateLoginHistory();
      MapleLoginBypassCoordinator.getInstance().runUpdateLoginBypass();
   }
}
