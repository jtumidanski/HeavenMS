package net.server.audit.locks.factory;

import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.active.TrackerReadLock;

public class MonitoredReadLockFactory {
   public static TrackerReadLock createLock(MonitoredReentrantReadWriteLock lock) {
      return new TrackerReadLock(lock);
   }
}
