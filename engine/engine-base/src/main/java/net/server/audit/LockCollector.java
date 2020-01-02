package net.server.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockCollector {

   private static final LockCollector instance = new LockCollector();
   private final Lock lock = new ReentrantLock(true);
   private Map<Runnable, Integer> disposableLocks = new HashMap<>(200);

   public static LockCollector getInstance() {
      return instance;
   }

   public void registerDisposeAction(Runnable r) {
      lock.lock();
      try {
         disposableLocks.put(r, 0);
      } finally {
         lock.unlock();
      }
   }

   public void runLockCollector() {
      List<Runnable> toDispose = new ArrayList<>();

      lock.lock();
      try {
         for (Entry<Runnable, Integer> e : disposableLocks.entrySet()) {
            Integer eVal = e.getValue();
            if (eVal > 5) {  // updates each 2min
               toDispose.add(e.getKey());
            } else {
               disposableLocks.put(e.getKey(), ++eVal);
            }
         }
      } finally {
         lock.unlock();
      }

      for (Runnable r : toDispose) {
         r.run();
      }
   }
}
