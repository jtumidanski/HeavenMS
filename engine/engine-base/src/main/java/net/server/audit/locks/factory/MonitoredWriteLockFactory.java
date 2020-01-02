package net.server.audit.locks.factory;

import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.active.TrackerWriteLock;

public class MonitoredWriteLockFactory {
   public static TrackerWriteLock createLock(MonitoredReentrantReadWriteLock lock) {
      return new TrackerWriteLock(lock);
   }
}
