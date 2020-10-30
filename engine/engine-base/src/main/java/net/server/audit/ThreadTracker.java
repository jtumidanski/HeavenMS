package net.server.audit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import net.server.audit.locks.MonitoredLockType;
import server.TimerManager;

/**
 * This tool has the main purpose of auditing deadlocks throughout the server and must be used only for debugging. The flag is USE_THREAD_TRACKER.
 */
public class ThreadTracker {
   private static ThreadTracker instance = null;
   private final Lock ttLock = new ReentrantLock(true);
   private final Map<Long, List<MonitoredLockType>> threadTracker = new HashMap<>();
   private final Map<Long, Integer> threadUpdate = new HashMap<>();
   private final Map<Long, Thread> threads = new HashMap<>();
   private final Map<Long, AtomicInteger> lockCount = new HashMap<>();
   private final Map<Long, MonitoredLockType> lockIds = new HashMap<>();
   private final Map<Long, Long> lockThreads = new HashMap<>();
   private final Map<Long, Integer> lockUpdate = new HashMap<>();
   private final Map<MonitoredLockType, Map<Long, Integer>> locks = new HashMap<>();
   ScheduledFuture<?> threadTrackerSchedule;

   public static ThreadTracker getInstance() {
      if (instance == null) {
         instance = new ThreadTracker();
      }
      return instance;
   }

   private static String printThreadLog(List<MonitoredLockType> stillLockedPath, String dateFormat) {
      StringBuilder s = new StringBuilder("----------------------------\r\n" + dateFormat + "\r\n    ");
      for (MonitoredLockType lock : stillLockedPath) {
         s.append(lock.name()).append(" ");
      }
      s.append("\r\n\r\n");

      return s.toString();
   }

   private static String printThreadStack(StackTraceElement[] list, String dateFormat) {
      StringBuilder s = new StringBuilder("----------------------------\r\n" + dateFormat + "\r\n");
      for (StackTraceElement stackTraceElement : list) {
         s.append("    ").append(stackTraceElement.toString()).append("\r\n");
      }

      return s.toString();
   }

   private String printThreadTrackerState(String dateFormat) {

      Map<MonitoredLockType, List<Integer>> lockValues = new HashMap<>();
      Set<Long> executingThreads = new HashSet<>();

      for (Map.Entry<Long, AtomicInteger> lc : lockCount.entrySet()) {
         if (lc.getValue().get() != 0) {
            executingThreads.add(lockThreads.get(lc.getKey()));

            MonitoredLockType lockId = lockIds.get(lc.getKey());
            List<Integer> list = lockValues.computeIfAbsent(lockId, k -> new ArrayList<>());
            list.add(lc.getValue().get());
         }
      }


      StringBuilder s = new StringBuilder("----------------------------\r\n" + dateFormat + "\r\n    ");
      s.append("Lock-thread usage count:");
      for (Map.Entry<MonitoredLockType, List<Integer>> lock : lockValues.entrySet()) {
         s.append("\r\n  ").append(lock.getKey().name()).append(": ");

         for (Integer i : lock.getValue()) {
            s.append(i).append(" ");
         }
      }
      s.append("\r\n\r\nThread opened lock path:");

      for (Long tid : executingThreads) {
         s.append("\r\n");
         for (MonitoredLockType lockId : threadTracker.get(tid)) {
            s.append(lockId.name()).append(" ");
         }
         s.append("|");
      }

      s.append("\r\n\r\n");

      return s.toString();
   }

   public void accessThreadTracker(boolean update, boolean lock, MonitoredLockType lockId, long lockOid) {
      ttLock.lock();
      try {
         if (update) {
            if (!lock) { // update tracker
               List<Long> toRemove = new ArrayList<>();

               for (Long l : threadUpdate.keySet()) {
                  int next = threadUpdate.get(l) + 1;
                  if (next == 4) {
                     List<MonitoredLockType> tt = threadTracker.get(l);

                     if (tt.isEmpty()) {
                        toRemove.add(l);
                     } else {
                        StackTraceElement[] ste = threads.get(l).getStackTrace();
                        if (ste.length > 0) {
                           DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                           dateFormat.setTimeZone(TimeZone.getDefault());
                           String df = dateFormat.format(new Date());
                           LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.DEADLOCK_LOCKS, printThreadLog(tt, df));
                           LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.DEADLOCK_STACK, printThreadStack(ste, df));
                        }
                     }
                  }

                  threadUpdate.put(l, next);
               }

               for (Long l : toRemove) {
                  threadTracker.remove(l);
                  threadUpdate.remove(l);
                  threads.remove(l);

                  for (Map<Long, Integer> threadLock : locks.values()) {
                     threadLock.remove(l);
                  }
               }

               toRemove.clear();

               for (Entry<Long, Integer> it : lockUpdate.entrySet()) {
                  int val = it.getValue() + 1;

                  if (val < 60) {
                     lockUpdate.put(it.getKey(), val);
                  } else {
                     toRemove.add(it.getKey());  // free the structure after 60 silent updates
                  }
               }

               for (Long l : toRemove) {
                  lockCount.remove(l);
                  lockIds.remove(l);
                  lockThreads.remove(l);
                  lockUpdate.remove(l);
               }
            } else {    // print status
               DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
               dateFormat.setTimeZone(TimeZone.getDefault());
               LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.DEADLOCK_STATE, printThreadTrackerState(dateFormat.format(new Date())));
            }
         } else {
            long tid = Thread.currentThread().getId();

            if (lock) {
               AtomicInteger c = lockCount.get(lockOid);
               if (c == null) {
                  c = new AtomicInteger(0);
                  lockCount.put(lockOid, c);
                  lockIds.put(lockOid, lockId);
                  lockThreads.put(lockOid, tid);
                  lockUpdate.put(lockOid, 0);
               }
               c.incrementAndGet();

               List<MonitoredLockType> list = threadTracker.get(tid);
               if (list == null) {
                  list = new ArrayList<>(5);
                  threadTracker.put(tid, list);
                  threadUpdate.put(tid, 0);
                  threads.put(tid, Thread.currentThread());
               } else if (list.isEmpty()) {
                  threadUpdate.put(tid, 0);
               }
               list.add(lockId);

               Map<Long, Integer> threadLock = locks.computeIfAbsent(lockId, k -> new HashMap<>(5));
               threadLock.merge(tid, 1, Integer::sum);
            } else {
               AtomicInteger c = lockCount.get(lockOid);
               if (c != null) {
                  c.decrementAndGet();
               }

               lockUpdate.put(lockOid, 0);

               List<MonitoredLockType> list = threadTracker.get(tid);
               for (int i = list.size() - 1; i >= 0; i--) {
                  if (lockId.equals(list.get(i))) {
                     list.remove(i);
                     break;
                  }
               }

               Map<Long, Integer> threadLock = locks.get(lockId);
               threadLock.put(tid, threadLock.get(tid) - 1);
            }
         }
      } finally {
         ttLock.unlock();
      }
   }

   private String printLockStatus(MonitoredLockType lockId) {
      StringBuilder s = new StringBuilder();

      for (Long threadId : locks.get(lockId).keySet()) {
         for (MonitoredLockType lockType : threadTracker.get(threadId)) {
            s.append("  ").append(lockType.name());
         }

         s.append(" |\r\n");
      }

      return s.toString();
   }

   public void registerThreadTrackerTask() {
      threadTrackerSchedule = TimerManager.getInstance().register(() -> accessThreadTracker(true, false, MonitoredLockType.UNDEFINED, -1), 10000, 10000);
   }

   public void cancelThreadTrackerTask() {
      threadTrackerSchedule.cancel(false);
   }
}
