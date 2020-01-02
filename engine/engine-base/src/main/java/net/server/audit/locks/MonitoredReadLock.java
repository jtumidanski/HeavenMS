package net.server.audit.locks;

public interface MonitoredReadLock {
   void lock();

   void unlock();

   boolean tryLock();

   MonitoredReadLock dispose();
}
