package net.server.audit.locks.empty;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredWriteLock;
import tools.FilePrinter;

public class EmptyWriteLock extends AbstractEmptyLock implements MonitoredWriteLock {
   private final MonitoredLockType id;

   public EmptyWriteLock(MonitoredLockType type) {
      this.id = type;
   }

   @Override
   public void lock() {
      FilePrinter.printError(FilePrinter.DISPOSED_LOCKS, "Captured locking tentative on disposed lock " + id + ":" + printThreadStack(Thread.currentThread().getStackTrace()));
   }

   @Override
   public void unlock() {
   }

   @Override
   public boolean tryLock() {
      FilePrinter.printError(FilePrinter.DISPOSED_LOCKS, "Captured try-locking tentative on disposed lock " + id + ":" + printThreadStack(Thread.currentThread().getStackTrace()));
      return false;
   }

   @Override
   public MonitoredWriteLock dispose() {
      return this;
   }
}
