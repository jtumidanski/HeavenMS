package net.server.audit.locks;

public interface MonitoredReentrantLock {
   void lock();

   void unlock();

   boolean tryLock();

   MonitoredReentrantLock dispose();
}
