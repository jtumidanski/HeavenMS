package net.server.audit.locks.empty;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredWriteLock;

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
