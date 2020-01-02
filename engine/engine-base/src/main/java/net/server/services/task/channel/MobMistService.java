package net.server.services.task.channel;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.services.BaseScheduler;
import net.server.services.BaseService;

public class MobMistService extends BaseService {

   private MobMistScheduler[] mobMistSchedulers = new MobMistScheduler[YamlConfig.config.server.CHANNEL_LOCKS];

   public MobMistService() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         mobMistSchedulers[i] = new MobMistScheduler();
      }
   }

   public void dispose() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         if (mobMistSchedulers[i] != null) {
            mobMistSchedulers[i].dispose();
            mobMistSchedulers[i] = null;
         }
      }
   }

   public void registerMobMistCancelAction(int mapId, Runnable runAction, long delay) {
      mobMistSchedulers[getChannelSchedulerIndex(mapId)].registerMistCancelAction(runAction, delay);
   }

   private class MobMistScheduler extends BaseScheduler {

      public MobMistScheduler() {
         super(MonitoredLockType.CHANNEL_MOB_MIST);
      }

      public void registerMistCancelAction(Runnable runAction, long delay) {
         registerEntry(runAction, runAction, delay);
      }

   }

}