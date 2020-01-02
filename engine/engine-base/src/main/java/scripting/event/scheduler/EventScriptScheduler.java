package scripting.event.scheduler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import config.YamlConfig;
import net.server.Server;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import server.ThreadManager;
import server.TimerManager;

public class EventScriptScheduler {

   private boolean disposed = false;
   private int idleProcesses = 0;
   private Map<Runnable, Long> registeredEntries = new HashMap<>();

   private ScheduledFuture<?> schedulerTask = null;
   private MonitoredReentrantLock schedulerLock;
   private Runnable monitorTask = this::runBaseSchedule;

   public EventScriptScheduler() {
      schedulerLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.EM_SCHEDULE, true);
   }

   private void runBaseSchedule() {
      List<Runnable> toRemove;
      Map<Runnable, Long> registeredEntriesCopy;

      schedulerLock.lock();
      try {
         if (registeredEntries.isEmpty()) {
            idleProcesses++;

            if (idleProcesses >= YamlConfig.config.server.MOB_STATUS_MONITOR_LIFE) {
               if (schedulerTask != null) {
                  schedulerTask.cancel(false);
                  schedulerTask = null;
               }
            }

            return;
         }

         idleProcesses = 0;
         registeredEntriesCopy = new HashMap<>(registeredEntries);
      } finally {
         schedulerLock.unlock();
      }

      long timeNow = Server.getInstance().getCurrentTime();
      toRemove = new LinkedList<>();
      for (Entry<Runnable, Long> rmd : registeredEntriesCopy.entrySet()) {
         if (rmd.getValue() < timeNow) {
            Runnable r = rmd.getKey();

            r.run();  // runs the scheduled action
            toRemove.add(r);
         }
      }

      if (!toRemove.isEmpty()) {
         schedulerLock.lock();
         try {
            for (Runnable r : toRemove) {
               registeredEntries.remove(r);
            }
         } finally {
            schedulerLock.unlock();
         }
      }
   }

   public void registerEntry(final Runnable scheduledAction, final long duration) {

      ThreadManager.getInstance().newTask(() -> {
         schedulerLock.lock();
         try {
            idleProcesses = 0;
            if (schedulerTask == null) {
               if (disposed) {
                  return;
               }

               schedulerTask = TimerManager.getInstance().register(monitorTask, YamlConfig.config.server.MOB_STATUS_MONITOR_PROC, YamlConfig.config.server.MOB_STATUS_MONITOR_PROC);
            }

            registeredEntries.put(scheduledAction, Server.getInstance().getCurrentTime() + duration);
         } finally {
            schedulerLock.unlock();
         }
      });
   }

   public void cancelEntry(final Runnable scheduledAction) {

      ThreadManager.getInstance().newTask(() -> {
         schedulerLock.lock();
         try {
            registeredEntries.remove(scheduledAction);
         } finally {
            schedulerLock.unlock();
         }
      });
   }

   public void dispose() {

      ThreadManager.getInstance().newTask(() -> {
         schedulerLock.lock();
         try {
            if (schedulerTask != null) {
               schedulerTask.cancel(false);
               schedulerTask = null;
            }

            registeredEntries.clear();
            disposed = true;
         } finally {
            schedulerLock.unlock();
         }

         disposeLocks();
      });
   }

   private void disposeLocks() {
      LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
   }

   private void emptyLocks() {
      schedulerLock = schedulerLock.dispose();
   }
}
