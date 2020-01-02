package net.server.audit.locks.factory;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.active.TrackerReentrantLock;

public class MonitoredReentrantLockFactory {
   public static TrackerReentrantLock createLock(MonitoredLockType id) {
      return new TrackerReentrantLock(id);
   }

   public static TrackerReentrantLock createLock(MonitoredLockType id, boolean fair) {
      return new TrackerReentrantLock(id, fair);
   }
}
