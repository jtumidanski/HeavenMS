package server.expeditions;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import database.administrator.BossLogDailyAdministrator;
import database.administrator.BossLogWeeklyAdministrator;
import database.provider.BossLogDailyProvider;
import database.provider.BossLogWeeklyProvider;
import config.YamlConfig;
import database.DatabaseConnection;
import tools.Pair;

public class MapleExpeditionBossLog {

   public static void resetBossLogTable() {
      Calendar thursday = Calendar.getInstance();
      thursday.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
      thursday.set(Calendar.HOUR, 0);
      thursday.set(Calendar.MINUTE, 0);
      thursday.set(Calendar.SECOND, 0);

      Calendar now = Calendar.getInstance();

      long weekLength = 7 * 24 * 60 * 60 * 1000;
      long halfDayLength = 12 * 60 * 60 * 1000;

      long deltaTime = now.getTime().getTime() - thursday.getTime().getTime();    // 2x time: get Date into millis
      deltaTime += halfDayLength;
      deltaTime %= weekLength;
      deltaTime -= halfDayLength;

      if (deltaTime < halfDayLength) {
         MapleExpeditionBossLog.resetBossLogTable(true, thursday);
      }

      now.set(Calendar.HOUR, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.SECOND, 0);

      MapleExpeditionBossLog.resetBossLogTable(false, now);
   }

   private static void resetBossLogTable(boolean week, Calendar c) {
      List<Pair<Timestamp, BossLogEntry>> resetTimestamps = BossLogEntry.getBossLogResetTimestamps(c, week);

      DatabaseConnection.getInstance().withConnection(connection -> resetTimestamps.forEach(pair -> {
         if (week) {
            BossLogWeeklyAdministrator.getInstance().deleteByAttemptTimeAndBossType(connection, pair.getLeft(), pair.getRight().name());
         } else {
            BossLogDailyAdministrator.getInstance().deleteByAttemptTimeAndBossType(connection, pair.getLeft(), pair.getRight().name());
         }
      }));
   }

   private static String getBossLogTable(boolean week) {
      return week ? "bosslog_weekly" : "bosslog_daily";
   }

   private static long countPlayerEntries(int cid, BossLogEntry boss) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         if (boss.week) {
            return BossLogWeeklyProvider.getInstance().countEntriesForCharacter(connection, cid, boss.name());
         } else {
            return BossLogDailyProvider.getInstance().countEntriesForCharacter(connection, cid, boss.name());
         }
      }).orElse(-1L);
   }

   private static void insertPlayerEntry(int cid, BossLogEntry boss) {
      DatabaseConnection.getInstance().withConnection(connection -> {
         if (boss.week) {
            BossLogWeeklyAdministrator.getInstance().addAttempt(connection, cid, boss.name());
         } else {
            BossLogDailyAdministrator.getInstance().addAttempt(connection, cid, boss.name());
         }
      });
   }

   public static boolean attemptBoss(int cid, int channel, MapleExpedition expedition, boolean log) {
      if (!YamlConfig.config.server.USE_ENABLE_DAILY_EXPEDITIONS) {
         return true;
      }

      BossLogEntry boss = BossLogEntry.getBossEntryByName(expedition.getType().name());
      if (boss == null) {
         return true;
      }

      if (channel < boss.minChannel || channel > boss.maxChannel) {
         return false;
      }

      if (countPlayerEntries(cid, boss) >= boss.entries) {
         return false;
      }

      if (log) {
         insertPlayerEntry(cid, boss);
      }
      return true;
   }

   public enum BossLogEntry {
      ZAKUM(2, 1, false),
      HORNTAIL(2, 1, false),
      PINK_BEAN(1, 1, false),
      SCARGA(1, 1, false),
      PAPULATUS(2, 1, false);

      private int entries;
      private int timeLength;
      private int minChannel, maxChannel;
      private boolean week;

      BossLogEntry(int entries, int timeLength, boolean week) {
         this(entries, 0, Integer.MAX_VALUE, timeLength, week);
      }

      BossLogEntry(int entries, int minChannel, int maxChannel, int timeLength, boolean week) {
         this.entries = entries;
         this.minChannel = minChannel;
         this.maxChannel = maxChannel;
         this.timeLength = timeLength;
         this.week = week;
      }

      private static List<Pair<Timestamp, BossLogEntry>> getBossLogResetTimestamps(Calendar timeNow, boolean week) {
         List<Pair<Timestamp, BossLogEntry>> resetTimestamps = new LinkedList<>();

         Timestamp ts = new Timestamp(timeNow.getTime().getTime());
         for (BossLogEntry b : BossLogEntry.values()) {
            if (b.week == week) {
               resetTimestamps.add(new Pair<>(ts, b));
            }
         }

         return resetTimestamps;
      }

      private static BossLogEntry getBossEntryByName(String name) {
         for (BossLogEntry b : BossLogEntry.values()) {
            if (name.contentEquals(b.name())) {
               return b;
            }
         }

         return null;
      }

   }
}
