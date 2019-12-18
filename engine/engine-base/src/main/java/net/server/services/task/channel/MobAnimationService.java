package net.server.services.task.channel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import config.YamlConfig;
import net.server.audit.LockCollector;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantLock;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import net.server.services.BaseScheduler;
import net.server.services.SchedulerListener;
import net.server.services.BaseService;

public class MobAnimationService extends BaseService {

   private MobAnimationScheduler[] mobAnimationSchedulers = new MobAnimationScheduler[YamlConfig.config.server.CHANNEL_LOCKS];

   public MobAnimationService() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         mobAnimationSchedulers[i] = new MobAnimationScheduler();
      }
   }

   public void dispose() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         if (mobAnimationSchedulers[i] != null) {
            mobAnimationSchedulers[i].dispose();
            mobAnimationSchedulers[i] = null;
         }
      }
   }

   public boolean registerMobOnAnimationEffect(int mapid, int mobHash, long delay) {
      return mobAnimationSchedulers[getChannelSchedulerIndex(mapid)].registerAnimationMode(mobHash, delay);
   }

   private static Runnable r = new Runnable() {
      @Override
      public void run() {
      }    // do nothing
   };

   private class MobAnimationScheduler extends BaseScheduler {
      Set<Integer> onAnimationMobs = new HashSet<>(1000);
      private MonitoredReentrantLock animationLock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CHANNEL_MOBANIMAT, true);

      public MobAnimationScheduler() {
         super(MonitoredLockType.CHANNEL_MOBACTION);

         super.addListener(new SchedulerListener() {
            @Override
            public void removedScheduledEntries(List<Object> toRemove, boolean update) {
               animationLock.lock();
               try {
                  for (Object hashObj : toRemove) {
                     Integer mobHash = (Integer) hashObj;
                     onAnimationMobs.remove(mobHash);
                  }
               } finally {
                  animationLock.unlock();
               }
            }
         });
      }

      public boolean registerAnimationMode(Integer mobHash, long animationTime) {
         animationLock.lock();
         try {
            if (onAnimationMobs.contains(mobHash)) {
               return false;
            }

            registerEntry(mobHash, r, animationTime);
            onAnimationMobs.add(mobHash);
            return true;
         } finally {
            animationLock.unlock();
         }
      }

      @Override
      public void dispose() {
         disposeLocks();
         super.dispose();
      }

      private void disposeLocks() {
         LockCollector.getInstance().registerDisposeAction(this::emptyLocks);
      }

      private void emptyLocks() {
         animationLock = animationLock.dispose();
      }

   }

}