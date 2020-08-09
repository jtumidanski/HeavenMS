package server.life;

import java.awt.Point;
import java.util.List;

public record MobSkill(int skillId, int level, List<Integer> summons, long coolTime, long duration, int hp,
                       int mpCon, int spawnEffect, int x, int y, float prop, int limit, Point lt, Point rb) {
   public Boolean makeChanceResult() {
      return prop == 1.0 || Math.random() < prop;
   }
}
