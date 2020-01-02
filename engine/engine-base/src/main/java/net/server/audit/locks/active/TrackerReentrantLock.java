package net.server.audit.locks.active;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import config.YamlConfig;
import net.server.audit.ThreadTracker;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.empty.EmptyReentrantLock;
import server.TimerManager;

public class TrackerReentrantLock extends ReentrantLock implements MonitoredReentrantLock {
   private final MonitoredLockType id;
   private final int hashcode;
   private final Lock state = new ReentrantLock(true);
   private final AtomicInteger reentrantCount = new AtomicInteger(0);
   private ScheduledFuture<?> timeoutSchedule = null;
   private StackTraceElement[] deadlockedState = null;

   public TrackerReentrantLock(MonitoredLockType id) {
      super();
      this.id = id;
      hashcode = this.hashCode();
   }

   public TrackerReentrantLock(MonitoredLockType id, boolean fair) {
      super(fair);
      this.id = id;
      hashcode = this.hashCode();
   }

   private static String printStackTrace(StackTraceElement[] list) {
      StringBuilder s = new StringBuilder();
      for (StackTraceElement stackTraceElement : list) {
         s.append("    ").append(stackTraceElement.toString()).append("\r\n");
      }

      return s.toString();
   }

   @Override
   public void lock() {
      if (YamlConfig.config.server.USE_THREAD_TRACKER) {
         if (deadlockedState != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getDefault());

            //FilePrinter.printError(FilePrinter.DEADLOCK_ERROR, "[CRITICAL] " + dateFormat.format(new Date()) + " Deadlock occurred when trying to use the '" + id.name() + "' lock resources:\r\n" + printStackTrace(deadlockedState));
            ThreadTracker.getInstance().accessThreadTracker(true, true, id, hashcode);
            deadlockedState = null;
         }

         registerLocking();
      }

      super.lock();
   }

   @Override
   public void unlock() {
      if (YamlConfig.config.server.USE_THREAD_TRACKER) {
         unregisterLocking();
      }

      super.unlock();
   }

   @Override
   public boolean tryLock() {
      if (super.tryLock()) {
         if (YamlConfig.config.server.USE_THREAD_TRACKER) {
            if (deadlockedState != null) {
               //FilePrinter.printError(FilePrinter.DEADLOCK_ERROR, "Deadlock occurred when trying to use the '" + id.name() + "' lock resources:\r\n" + printStackTrace(deadlockedState));
               ThreadTracker.getInstance().accessThreadTracker(true, true, id, hashcode);
               deadlockedState = null;
            }

            registerLocking();
         }
         return true;
      } else {
         return false;
      }
   }

   private void registerLocking() {
      state.lock();
      try {
         ThreadTracker.getInstance().accessThreadTracker(false, true, id, hashcode);

         if (reentrantCount.incrementAndGet() == 1) {
            final Thread t = Thread.currentThread();
            timeoutSchedule = TimerManager.getInstance().schedule(() -> issueDeadlock(t), YamlConfig.config.server.LOCK_MONITOR_TIME);
         }
      } finally {
         state.unlock();
      }
   }

   private void unregisterLocking() {
      state.lock();
      try {
         if (reentrantCount.decrementAndGet() == 0) {
            if (timeoutSchedule != null) {
               timeoutSchedule.cancel(false);
               timeoutSchedule = null;
            }
         }

         ThreadTracker.getInstance().accessThreadTracker(false, false, id, hashcode);
      } finally {
         state.unlock();
      }
   }

   private void issueDeadlock(Thread t) {
      deadlockedState = t.getStackTrace();
      //super.unlock();
   }

   @Override
   public MonitoredReentrantLock dispose() {
      state.lock();
      try {
         if (timeoutSchedule != null) {
            timeoutSchedule.cancel(false);
            timeoutSchedule = null;
         }

         reentrantCount.set(Integer.MAX_VALUE);
      } finally {
         state.unlock();
      }

      //unlock();
      return new EmptyReentrantLock(id);
   }
}
