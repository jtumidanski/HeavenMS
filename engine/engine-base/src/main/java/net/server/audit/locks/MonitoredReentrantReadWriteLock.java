package net.server.audit.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MonitoredReentrantReadWriteLock extends ReentrantReadWriteLock {
   public final MonitoredLockType id;

   public MonitoredReentrantReadWriteLock(MonitoredLockType id) {
      super();
      this.id = id;
   }

   public MonitoredReentrantReadWriteLock(MonitoredLockType id, boolean fair) {
      super(fair);
      this.id = id;
   }

   @Override
   public ReadLock readLock() {
      return super.readLock();
   }

   @Override
   public WriteLock writeLock() {
      return super.writeLock();
   }
}
