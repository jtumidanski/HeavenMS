package net.server.audit.locks.empty;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import tools.FilePrinter;

public class EmptyReentrantLock extends AbstractEmptyLock implements MonitoredReentrantLock {
   private final MonitoredLockType id;

   public EmptyReentrantLock(MonitoredLockType type) {
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
   public MonitoredReentrantLock dispose() {
      return this;
   }
}
