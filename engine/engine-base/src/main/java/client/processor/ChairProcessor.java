package client.processor;

import config.YamlConfig;
import constants.MapleJob;
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

   public Pair<Integer, Pair<Integer, Integer>> getChairTaskIntervalRate(int maxHp, int maxMp) {
      float toHeal = Math.max(maxHp, maxMp);
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
      if (maxHp > maxMp) {
         hpRegen = midRegen;
         mpRegen = (int) Math.ceil(maxMp / procs);
      } else {
         hpRegen = (int) Math.ceil(maxHp / procs);
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
