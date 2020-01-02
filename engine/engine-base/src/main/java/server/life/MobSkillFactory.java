package server.life;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReadLock;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;
import net.server.audit.locks.MonitoredWriteLock;
import net.server.audit.locks.factory.MonitoredReadLockFactory;
import net.server.audit.locks.factory.MonitoredWriteLockFactory;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

public class MobSkillFactory {
   private final static MapleDataProvider dataSource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Skill.wz"));
   private final static MonitoredReentrantReadWriteLock dataLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.MOB_SKILL_FACTORY);
   private final static MonitoredReadLock rL = MonitoredReadLockFactory.createLock(dataLock);
   private final static MonitoredWriteLock wL = MonitoredWriteLockFactory.createLock(dataLock);
   private static Map<String, MobSkill> mobSkills = new HashMap<>();
   private static MapleData skillRoot = dataSource.getData("MobSkill.img");

   public static MobSkill getMobSkill(final int skillId, final int level) {
      final String key = skillId + "" + level;
      rL.lock();
      try {
         MobSkill ret = mobSkills.get(key);
         if (ret != null) {
            return ret;
         }
      } finally {
         rL.unlock();
      }
      wL.lock();
      try {
         MobSkill ret;
         ret = mobSkills.get(key);
         if (ret == null) {
            MapleData skillData = skillRoot.getChildByPath(skillId + "/level/" + level);
            if (skillData != null) {
               int mpCon = MapleDataTool.getInt(skillData.getChildByPath("mpCon"), 0);
               List<Integer> toSummon = new ArrayList<>();
               for (int i = 0; i > -1; i++) {
                  if (skillData.getChildByPath(String.valueOf(i)) == null) {
                     break;
                  }
                  toSummon.add(MapleDataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0));
               }
               int effect = MapleDataTool.getInt("summonEffect", skillData, 0);
               int hp = MapleDataTool.getInt("hp", skillData, 100);
               int x = MapleDataTool.getInt("x", skillData, 1);
               int y = MapleDataTool.getInt("y", skillData, 1);
               long duration = MapleDataTool.getInt("time", skillData, 0) * 1000;
               long coolDownTime = MapleDataTool.getInt("interval", skillData, 0) * 1000;
               int iprop = MapleDataTool.getInt("prop", skillData, 100);
               float prop = iprop / 100;
               int limit = MapleDataTool.getInt("limit", skillData, 0);
               MapleData ltd = skillData.getChildByPath("lt");
               Point lt = null;
               Point rb = null;
               if (ltd != null) {
                  lt = (Point) ltd.getData();
                  rb = (Point) skillData.getChildByPath("rb").getData();
               }
               ret = new MobSkill(skillId, level, toSummon, coolDownTime, duration, hp, mpCon, effect, x, y, prop, limit, lt, rb);
            }
            mobSkills.put(skillId + "" + level, ret);
         }
         return ret;
      } finally {
         wL.unlock();
      }
   }
}
