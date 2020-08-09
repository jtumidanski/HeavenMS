package client;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

import server.TimerManager;

public class MapleCharacterScheduler {
   public enum Type {
      DRAGON_BLOOD,
      HP_DECREASE,
      BEHODLER_HEAL,
      BEHOLDER_BUFF,
      BERSERK,
      SKILL_COOL_DOWN,
      BUFF_EXPIRE,
      ITEM_EXPIRE,
      DISEASE_EXPIRE,
      QUEST_EXPIRE,
      RECOVERY,
      EXTRA_RECOVERY,
      CHAIR_RECOVERY,
      PENDANT_OF_SPIRIT,
      CARNIVAL_PQ
   }

   private ScheduledFuture<?> dragonBloodSchedule;

   private ScheduledFuture<?> hpDecreaseTask;

   private ScheduledFuture<?> beholderHealingSchedule, beholderBuffSchedule, berserkSchedule;

   private ScheduledFuture<?> skillCoolDownTask = null;

   private ScheduledFuture<?> buffExpireTask = null;

   private ScheduledFuture<?> itemExpireTask = null;

   private ScheduledFuture<?> diseaseExpireTask = null;

   private ScheduledFuture<?> questExpireTask = null;

   private ScheduledFuture<?> recoveryTask = null;

   private ScheduledFuture<?> extraRecoveryTask = null;

   private ScheduledFuture<?> chairRecoveryTask = null;

   private ScheduledFuture<?> pendantOfSpirit = null; //1122017

   private ScheduledFuture<?> cpqSchedule = null;

   public void add(Type type, ScheduledFuture scheduledFuture) {
      set(type, scheduledFuture);
   }

   public void add(Type type, Runnable runnable, long repeatTime, long delay) {
      set(type, TimerManager.getInstance().register(runnable, repeatTime, delay));
   }

   public void add(Type type, Runnable runnable, long delay) {
      set(type, TimerManager.getInstance().register(runnable, delay));
   }

   public void addIfNotExists(Type type, Runnable runnable, long repeatTime, long delay) {
      if (get(type) == null) {
         add(type, runnable, repeatTime, delay);
      }
   }

   public void addIfNotExists(Type type, Runnable runnable, long delay) {
      if (get(type) == null) {
         add(type, runnable, delay);
      }
   }

   public void cancel(Type type) {
      ScheduledFuture<?> future = get(type);
      if (future != null) {
         future.cancel(false);
         set(type, null);
      }
   }

   public void cancelAll() {
      Arrays.stream(Type.values()).forEach(this::cancel);
   }

   private ScheduledFuture<?> get(Type type) {
      return switch (type) {
         case DRAGON_BLOOD -> dragonBloodSchedule;
         case HP_DECREASE -> hpDecreaseTask;
         case BEHODLER_HEAL -> beholderHealingSchedule;
         case BEHOLDER_BUFF -> beholderBuffSchedule;
         case BERSERK -> berserkSchedule;
         case SKILL_COOL_DOWN -> skillCoolDownTask;
         case BUFF_EXPIRE -> buffExpireTask;
         case ITEM_EXPIRE -> itemExpireTask;
         case DISEASE_EXPIRE -> diseaseExpireTask;
         case QUEST_EXPIRE -> questExpireTask;
         case RECOVERY -> recoveryTask;
         case EXTRA_RECOVERY -> extraRecoveryTask;
         case CHAIR_RECOVERY -> chairRecoveryTask;
         case PENDANT_OF_SPIRIT -> pendantOfSpirit;
         case CARNIVAL_PQ -> cpqSchedule;
      };
   }

   private void set(Type type, ScheduledFuture<?> future) {
      switch (type) {
         case DRAGON_BLOOD:
            dragonBloodSchedule = future;
         case HP_DECREASE:
            hpDecreaseTask = future;
         case BEHODLER_HEAL:
            beholderHealingSchedule = future;
         case BEHOLDER_BUFF:
            beholderBuffSchedule = future;
         case BERSERK:
            berserkSchedule = future;
         case SKILL_COOL_DOWN:
            skillCoolDownTask = future;
         case BUFF_EXPIRE:
            buffExpireTask = future;
         case ITEM_EXPIRE:
            itemExpireTask = future;
         case DISEASE_EXPIRE:
            diseaseExpireTask = future;
         case QUEST_EXPIRE:
            questExpireTask = future;
         case RECOVERY:
            recoveryTask = future;
         case EXTRA_RECOVERY:
            extraRecoveryTask = future;
         case CHAIR_RECOVERY:
            chairRecoveryTask = future;
         case PENDANT_OF_SPIRIT:
            pendantOfSpirit = future;
         case CARNIVAL_PQ:
            cpqSchedule = future;
      }
   }
}
