package net.server.channel.services.task;

import config.YamlConfig;
import net.server.audit.locks.MonitoredLockType;
import net.server.channel.services.BaseScheduler;

public class MobClearSkillService extends BaseService {

   private MobClearSkillScheduler mobClearSkillSchedulers[] = new MobClearSkillScheduler[YamlConfig.config.server.CHANNEL_LOCKS];

   public MobClearSkillService() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         mobClearSkillSchedulers[i] = new MobClearSkillScheduler();
      }
   }

   public void dispose() {
      for (int i = 0; i < YamlConfig.config.server.CHANNEL_LOCKS; i++) {
         if (mobClearSkillSchedulers[i] != null) {
            mobClearSkillSchedulers[i].dispose();
            mobClearSkillSchedulers[i] = null;
         }
      }
   }

   public void registerMobClearSkillAction(int mapid, Runnable runAction, long delay) {
      mobClearSkillSchedulers[getChannelSchedulerIndex(mapid)].registerClearSkillAction(runAction, delay);
   }

   private class MobClearSkillScheduler extends BaseScheduler {

      public MobClearSkillScheduler() {
         super(MonitoredLockType.CHANNEL_MOBSKILL);
      }

      public void registerClearSkillAction(Runnable runAction, long delay) {
         registerEntry(runAction, runAction, delay);
      }

   }

}