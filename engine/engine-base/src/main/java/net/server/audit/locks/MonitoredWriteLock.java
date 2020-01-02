package net.server.audit.locks;

public interface MonitoredWriteLock {
   void lock();

   void unlock();

   boolean tryLock();

   MonitoredWriteLock dispose();
}
