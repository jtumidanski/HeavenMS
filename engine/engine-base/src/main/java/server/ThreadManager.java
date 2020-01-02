package server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {
   private static ThreadManager instance = new ThreadManager();
   private ThreadPoolExecutor threadPoolExecutor;

   private ThreadManager() {
   }

   public static ThreadManager getInstance() {
      return instance;
   }

   public void newTask(Runnable runnable) {
      threadPoolExecutor.execute(runnable);
   }

   public void start() {
      ThreadFactory threadFactory = Executors.defaultThreadFactory();
      threadPoolExecutor = new ThreadPoolExecutor(20, 1000, 77, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), threadFactory,
            (runnable, executor) -> {
               Thread thread = new Thread(runnable);
               thread.start();
            });
   }

   public void stop() {
      threadPoolExecutor.shutdown();
      try {
         threadPoolExecutor.awaitTermination(5, TimeUnit.MINUTES);
      } catch (InterruptedException ignored) {
      }
   }
}
