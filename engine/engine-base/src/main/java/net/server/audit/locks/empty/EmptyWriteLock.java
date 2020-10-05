package net.server.audit.locks.empty;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredWriteLock;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public class EmptyWriteLock extends AbstractEmptyLock implements MonitoredWriteLock {
   private final MonitoredLockType id;

   public EmptyWriteLock(MonitoredLockType type) {
      this.id = type;
   }

   @Override
   public void lock() {
      LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.DISPOSED_LOCKS, "Captured locking tentative on disposed lock " + id + ":" + printThreadStack(Thread.currentThread().getStackTrace()));
   }

   @Override
   public void unlock() {
   }

   @Override
   public boolean tryLock() {
      LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.DISPOSED_LOCKS, "Captured try-locking tentative on disposed lock " + id + ":" + printThreadStack(Thread.currentThread().getStackTrace()));
      return false;
   }

   @Override
   public MonitoredWriteLock dispose() {
      return this;
   }
}
