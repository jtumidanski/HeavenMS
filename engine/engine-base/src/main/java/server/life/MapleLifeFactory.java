package server.life;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import provider.wz.MapleDataType;
import server.life.builder.MapleMonsterStatsBuilder;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.Pair;
import tools.StringUtil;

public class MapleLifeFactory {

   private final static MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/String.wz"));
   private static MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/Mob.wz"));
   private static MapleData mobStringData = stringDataWZ.getData("Mob.img");
   private static MapleData npcStringData = stringDataWZ.getData("Npc.img");
   private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();
   private static Set<Integer> hpBarBosses = getHpBarBosses();

   private static Set<Integer> getHpBarBosses() {
      Set<Integer> ret = new HashSet<>();

      MapleDataProvider uiDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wzpath") + "/UI.wz"));
      for (MapleData bossData : uiDataWZ.getData("UIWindow.img").getChildByPath("MobGage/Mob").getChildren()) {
         ret.add(Integer.valueOf(bossData.getName()));
      }

      return ret;
   }

   public static AbstractLoadedMapleLife getLife(int id, String type) {
      if (type.equalsIgnoreCase("n")) {
         return getNPC(id);
      } else if (type.equalsIgnoreCase("m")) {
         return getMonster(id).orElseThrow();
      } else {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Unknown Life type: " + type);
         return null;
      }
   }

   private static void setMonsterAttackInfo(int mid, List<MobAttackInfoHolder> attackInfos) {
      if (!attackInfos.isEmpty()) {
         MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();

         for (MobAttackInfoHolder attackInfo : attackInfos) {
            mi.setMobAttackInfo(mid, attackInfo.attackPos(), attackInfo.mpCon(), attackInfo.coolTime());
            mi.setMobAttackAnimationTime(mid, attackInfo.attackPos(), attackInfo.animationTime());
         }
      }
   }

   private static Pair<MapleMonsterStats, List<MobAttackInfoHolder>> getMonsterStats(int mid) {
      MapleData monsterData = data.getData(StringUtil.getLeftPaddedStr(mid + ".img", '0', 11));
      if (monsterData == null) {
         return null;
      }
      MapleData monsterInfoData = monsterData.getChildByPath("info");

      List<MobAttackInfoHolder> attackInfos = new LinkedList<>();

      int linkMid = MapleDataTool.getIntConvert("link", monsterInfoData, 0);

      if (linkMid != 0) {
         Pair<MapleMonsterStats, List<MobAttackInfoHolder>> linkStats = getMonsterStats(linkMid);
         if (linkStats == null) {
            return null;
         }

         attackInfos.addAll(linkStats.getRight());
      }

      MapleMonsterStatsBuilder builder = new MapleMonsterStatsBuilder()
            .setHp(MapleDataTool.getIntConvert("maxHP", monsterInfoData))
            .setFriendly(MapleDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) == 1)
            .setPaDamage(MapleDataTool.getIntConvert("PADamage", monsterInfoData))
            .setPdDamage(MapleDataTool.getIntConvert("PDDamage", monsterInfoData))
            .setMaDamage(MapleDataTool.getIntConvert("MADamage", monsterInfoData))
            .setMdDamage(MapleDataTool.getIntConvert("MDDamage", monsterInfoData))
            .setMp(MapleDataTool.getIntConvert("maxMP", monsterInfoData, 0))
            .setExp(MapleDataTool.getIntConvert("exp", monsterInfoData, 0))
            .setLevel(MapleDataTool.getIntConvert("level", monsterInfoData))
            .setRemoveAfter(MapleDataTool.getIntConvert("removeAfter", monsterInfoData, 0));

      boolean isBoss = MapleDataTool.getIntConvert("boss", monsterInfoData, 0) > 0;
      builder.setBoss(isBoss)
            .setExplosiveReward(MapleDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0)
            .setFFALoot(MapleDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0)
            .setUndead(MapleDataTool.getIntConvert("undead", monsterInfoData, 0) > 0)
            .setName(MapleDataTool.getString(mid + "/name", mobStringData, "MISSINGNO"))
            .setBuffToGive(MapleDataTool.getIntConvert("buff", monsterInfoData, -1))
            .setCp(MapleDataTool.getIntConvert("getCP", monsterInfoData, 0))
            .setRemoveOnMiss(MapleDataTool.getIntConvert("removeOnMiss", monsterInfoData, 0) > 0);

      MapleData special = monsterInfoData.getChildByPath("coolDamage");
      if (special != null) {
         int coolDmg = MapleDataTool.getIntConvert("coolDamage", monsterInfoData);
         int coolProb = MapleDataTool.getIntConvert("coolDamageProb", monsterInfoData, 0);
         builder.setCool(coolDmg, coolProb);
      }
      special = monsterInfoData.getChildByPath("loseItem");
      if (special != null) {
         for (MapleData liData : special.getChildren()) {
            builder.addLoseItem(new LoseItem(MapleDataTool.getInt(liData.getChildByPath("id")), (byte) MapleDataTool.getInt(liData.getChildByPath("prop")), (byte) MapleDataTool.getInt(liData.getChildByPath("x"))));
         }
      }
      special = monsterInfoData.getChildByPath("selfDestruction");
      if (special != null) {
         builder.setSelfDestruction(new SelfDestruction((byte) MapleDataTool.getInt(special.getChildByPath("action")), MapleDataTool.getIntConvert("removeAfter", special, -1), MapleDataTool.getIntConvert("hp", special, -1)));
      }
      MapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");
      int firstAttack = 0;
      if (firstAttackData != null) {
         if (firstAttackData.getType() == MapleDataType.FLOAT) {
            firstAttack = Math.round(MapleDataTool.getFloat(firstAttackData));
         } else {
            firstAttack = MapleDataTool.getInt(firstAttackData);
         }
      }
      builder.setFirstAttack(firstAttack > 0);
      builder.setDropPeriod(MapleDataTool.getIntConvert("dropItemPeriod", monsterInfoData, 0) * 10000);

      boolean hpBarBoss = isBoss && hpBarBosses.contains(mid);
      builder.setTagColor((byte) (hpBarBoss ? MapleDataTool.getIntConvert("hpTagColor", monsterInfoData, 0) : 0));
      builder.setTagBackgroundColor((byte) (hpBarBoss ? MapleDataTool.getIntConvert("hpTagBgcolor", monsterInfoData, 0) : 0));

      for (MapleData data : monsterData) {
         if (!data.getName().equals("info")) {
            int delay = 0;
            for (MapleData pic : data.getChildren()) {
               delay += MapleDataTool.getIntConvert("delay", pic, 0);
            }
            builder.setAnimationTime(data.getName(), delay);
         }
      }
      MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
      if (reviveInfo != null) {
         List<Integer> revives = new LinkedList<>();
         for (MapleData data_ : reviveInfo) {
            revives.add(MapleDataTool.getInt(data_));
         }
         builder.setRevives(revives);
      }

      String elemAttr = MapleDataTool.getString("elemAttr", monsterInfoData, "");
      for (int i = 0; i < elemAttr.length(); i += 2) {
         builder.setEffectiveness(Element.getFromChar(elemAttr.charAt(i)), ElementalEffectiveness.getByNumber(Integer.parseInt(String.valueOf(elemAttr.charAt(i + 1)))));
      }

      MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
      MapleData monsterSkillInfoData = monsterInfoData.getChildByPath("skill");
      if (monsterSkillInfoData != null) {
         int i = 0;
         List<Pair<Integer, Integer>> skills = new ArrayList<>();
         while (monsterSkillInfoData.getChildByPath(Integer.toString(i)) != null) {
            int skillId = MapleDataTool.getInt(i + "/skill", monsterSkillInfoData, 0);
            int skillLv = MapleDataTool.getInt(i + "/level", monsterSkillInfoData, 0);
            skills.add(new Pair<>(skillId, skillLv));

            MapleData monsterSkillData = monsterData.getChildByPath("skill" + (i + 1));
            if (monsterSkillData != null) {
               int animationTime = 0;
               for (MapleData effectEntry : monsterSkillData.getChildren()) {
                  animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
               }

               MobSkill skill = MobSkillFactory.getMobSkill(skillId, skillLv);
               mi.setMobSkillAnimationTime(skill, animationTime);
            }

            i++;
         }
         builder.setSkills(skills);
      }

      int i = 0;
      MapleData monsterAttackData;
      while ((monsterAttackData = monsterData.getChildByPath("attack" + (i + 1))) != null) {
         int animationTime = 0;
         for (MapleData effectEntry : monsterAttackData.getChildren()) {
            animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
         }

         int mpCon = MapleDataTool.getIntConvert("info/conMP", monsterAttackData, 0);
         int coolTime = MapleDataTool.getIntConvert("info/attackAfter", monsterAttackData, 0);
         attackInfos.add(new MobAttackInfoHolder(i, mpCon, coolTime, animationTime));
         i++;
      }

      MapleData banishData = monsterInfoData.getChildByPath("ban");
      if (banishData != null) {
         builder.setBanish(new BanishInfo(MapleDataTool.getString("banMsg", banishData), MapleDataTool.getInt("banMap/0/field", banishData, -1), MapleDataTool.getString("banMap/0/portal", banishData, "sp")));
      }

      int noFlip = MapleDataTool.getInt("noFlip", monsterInfoData, 0);
      if (noFlip > 0) {
         Point origin = MapleDataTool.getPoint("stand/0/origin", monsterData, null);
         if (origin != null) {
            builder.setFixedStance(origin.getX() < 1 ? 5 : 4);    // fixed left/right
         }
      }

      return new Pair<>(builder.build(), attackInfos);
   }

   public static Optional<MapleMonster> getMonster(int monsterId) {
      try {
         MapleMonsterStats stats = monsterStats.get(monsterId);
         if (stats == null) {
            Pair<MapleMonsterStats, List<MobAttackInfoHolder>> mobStats = getMonsterStats(monsterId);
            if (mobStats == null) {
               return Optional.empty();
            }

            stats = mobStats.getLeft();
            setMonsterAttackInfo(monsterId, mobStats.getRight());

            monsterStats.put(monsterId, stats);
         }
         return Optional.of(new MapleMonster(monsterId, stats));
      } catch (NullPointerException npe) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "[SEVERE] MOB " + monsterId + " failed to load. Issue: " + npe.getMessage() + "\n\n");
         npe.printStackTrace();
         return Optional.empty();
      }
   }

   public static int getMonsterLevel(int mid) {
      try {
         MapleMonsterStats stats = monsterStats.get(mid);
         if (stats == null) {
            MapleData monsterData = data.getData(StringUtil.getLeftPaddedStr(mid + ".img", '0', 11));
            if (monsterData == null) {
               return -1;
            }
            MapleData monsterInfoData = monsterData.getChildByPath("info");
            return MapleDataTool.getIntConvert("level", monsterInfoData);
         } else {
            return stats.level();
         }
      } catch (NullPointerException npe) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "[SEVERE] MOB " + mid + " failed to load. Issue: " + npe.getMessage() + "\n\n");
         npe.printStackTrace();
      }

      return -1;
   }

   public static MapleNPC getNPC(int nid) {
      return new MapleNPC(nid, new MapleNPCStats(MapleDataTool.getString(nid + "/name", npcStringData, "MISSINGNO")));
   }

   public static String getNPCDefaultTalk(int nid) {
      return MapleDataTool.getString(nid + "/d0", npcStringData, "(...)");
   }
}
