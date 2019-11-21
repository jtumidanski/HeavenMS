package client.processor;

import client.MapleJob;
import config.YamlConfig;
import constants.skills.Beginner;
import constants.skills.Legend;
import constants.skills.Noblesse;
import tools.Pair;

public class ChairProcessor {
   private static ChairProcessor ourInstance = new ChairProcessor();

   public static ChairProcessor getInstance() {
      return ourInstance;
   }

   private ChairProcessor() {
   }

   public Pair<Integer, Pair<Integer, Integer>> getChairTaskIntervalRate(int maxhp, int maxmp) {
      float toHeal = Math.max(maxhp, maxmp);
      float maxDuration = YamlConfig.config.server.CHAIR_EXTRA_HEAL_MAX_DELAY * 1000;

      int rate = 0;
      int minRegen = 1, maxRegen = (256 * YamlConfig.config.server.CHAIR_EXTRA_HEAL_MULTIPLIER) - 1, midRegen = 1;
      while (minRegen < maxRegen) {
         midRegen = (int) ((minRegen + maxRegen) * 0.94);

         float procs = toHeal / midRegen;
         float newRate = maxDuration / procs;
         rate = (int) newRate;

         if (newRate < 420) {
            minRegen = (int) (1.2 * midRegen);
         } else if (newRate > 5000) {
            maxRegen = (int) (0.8 * midRegen);
         } else {
            break;
         }
      }

      float procs = maxDuration / rate;
      int hpRegen, mpRegen;
      if (maxhp > maxmp) {
         hpRegen = midRegen;
         mpRegen = (int) Math.ceil(maxmp / procs);
      } else {
         hpRegen = (int) Math.ceil(maxhp / procs);
         mpRegen = midRegen;
      }

      return new Pair<>(rate, new Pair<>(hpRegen, mpRegen));
   }

   public int getJobMapChair(MapleJob job) {
      switch (job.getId() / 1000) {
         case 0:
            return Beginner.MAP_CHAIR;
         case 1:
            return Noblesse.MAP_CHAIR;
         default:
            return Legend.MAP_CHAIR;
      }
   }
}
