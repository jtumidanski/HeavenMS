/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package server.expeditions;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import client.database.administrator.BossLogDailyAdministrator;
import client.database.administrator.BossLogWeeklyAdministrator;
import client.database.provider.BossLogDailyProvider;
import client.database.provider.BossLogWeeklyProvider;
import constants.ServerConstants;
import tools.DatabaseConnection;
import tools.Pair;

/**
 * @author Conrad
 * @author Ronan
 */
public class MapleExpeditionBossLog {

   public static void resetBossLogTable() {
        /*
        Boss logs resets 12am, weekly thursday 12AM - thanks Smitty Werbenjagermanjensen (superadlez) - https://www.reddit.com/r/Maplestory/comments/61tiup/about_reset_time/
        */

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

   public static boolean attemptBoss(int cid, int channel, MapleExpedition exped, boolean log) {
      if (!ServerConstants.USE_ENABLE_DAILY_EXPEDITIONS) {
         return true;
      }

      BossLogEntry boss = BossLogEntry.getBossEntryByName(exped.getType().name());
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
      PINKBEAN(1, 1, false),
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
