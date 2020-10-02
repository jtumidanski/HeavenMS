/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client.autoban;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import config.YamlConfig;
import net.server.Server;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.StringUtil;

public class AutoBanManager {
   private MapleCharacter chr;
   private Map<AutoBanFactory, Integer> points = new HashMap<>();
   private Map<AutoBanFactory, Long> lastTime = new HashMap<>();
   private int misses = 0;
   private int lastMisses = 0;
   private int sameMissCount = 0;
   private long[] spam = new long[20];
   private int[] timestamp = new int[20];
   private byte[] timestampCounter = new byte[20];

   public AutoBanManager(MapleCharacter chr) {
      this.chr = chr;
   }

   public void addPoint(AutoBanFactory fac, String reason) {
      if (YamlConfig.config.server.USE_AUTOBAN) {
         if (chr.isGM() || chr.isBanned()) {
            return;
         }

         if (lastTime.containsKey(fac)) {
            if (lastTime.get(fac) < (Server.getInstance().getCurrentTime() - fac.getExpire())) {
               points.put(fac, points.get(fac) / 2); //So the points are not completely gone.
            }
         }
         if (fac.getExpire() != -1) {
            lastTime.put(fac, Server.getInstance().getCurrentTime());
         }

         if (points.containsKey(fac)) {
            points.put(fac, points.get(fac) + 1);
         } else {
            points.put(fac, 1);
         }

         if (points.get(fac) >= fac.getMaximum()) {
            chr.autoBan(reason);
            //chr.autoBan("Auto banned for " + fac.name() + " ;" + reason, 1);
            //chr.sendPolice("You have been blocked by #bMooplePolice for the HACK reason#k.");
         }
      }
      if (YamlConfig.config.server.USE_AUTOBAN_LOG) {
         // Lets log every single point too.
         LoggerUtil.printInfo(LoggerOriginator.AUTOBAN_WARNING, StringUtil.makeMapleReadable(chr.getName()) + " caused " + fac.name() + " " + reason);
      }
   }

   public void addMiss() {
      this.misses++;
   }

   public void resetMisses() {
      if (lastMisses == misses && misses > 6) {
         sameMissCount++;
      }
      if (sameMissCount > 4) {
         chr.sendPolice("You will be disconnected for miss god mode.");
      }
      //chr.autoBan("Auto banned for : " + misses + " Miss god mode", 1);
      else if (sameMissCount > 0) {
         this.lastMisses = misses;
      }
      this.misses = 0;
   }

   //Don't use the same type for more than 1 thing
   public void spam(int type) {
      this.spam[type] = Server.getInstance().getCurrentTime();
   }

   public void spam(int type, int timestamp) {
      this.spam[type] = timestamp;
   }

   public long getLastSpam(int type) {
      return spam[type];
   }

   /**
    * Timestamp checker
    *
    * <code>type</code>:<br>
    * 1: Pet Food<br>
    * 2: InventoryMerge<br>
    * 3: InventorySort<br>
    * 4: SpecialMove<br>
    * 5: UseCatchItem<br>
    * 6: Item Drop<br>
    * 7: Chat<br>
    * 8: HealOverTimeHP<br>
    * 9: HealOverTimeMP<br>
    *
    * @param type type
    */
   public void setTimestamp(int type, int time, int times) {
      if (this.timestamp[type] == time) {
         this.timestampCounter[type]++;
         if (this.timestampCounter[type] >= times) {
            if (YamlConfig.config.server.USE_AUTOBAN) {
               chr.getClient().disconnect(false, false);
            }
            LoggerUtil.printError(LoggerOriginator.EXPLOITS, "Player " + chr + " was caught spamming TYPE " + type + " and has been disconnected.");
         }
      } else {
         this.timestamp[type] = time;
         this.timestampCounter[type] = 0;
      }
   }
}
