package net.server.channel.services.task;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.channel.services.BaseScheduler;

public class MobMistService extends BaseService {

   private MobMistScheduler mobMistSchedulers[] = new MobMistScheduler[YamlConfig.config.server.CHANNEL_LOCKS];

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

   public void registerMobMistCancelAction(int mapid, Runnable runAction, long delay) {
      mobMistSchedulers[getChannelSchedulerIndex(mapid)].registerMistCancelAction(runAction, delay);
   }

   private class MobMistScheduler extends BaseScheduler {

      public MobMistScheduler() {
         super(MonitoredLockType.CHANNEL_MOBMIST);
      }

      public void registerMistCancelAction(Runnable runAction, long delay) {
         registerEntry(runAction, runAction, delay);
      }

   }

}