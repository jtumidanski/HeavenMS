package net.server.task;

import net.server.audit.LockCollector;

public class ReleaseLockTask implements Runnable {
   @Override
   public void run() {
      LockCollector.getInstance().runLockCollector();
   }
}
