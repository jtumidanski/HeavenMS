/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.life;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import client.database.provider.DropDataProvider;
import constants.ItemConstants;
import constants.ServerConstants;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import tools.DatabaseConnection;
import tools.Pair;
import tools.Randomizer;

public class MapleMonsterInformationProvider {
   private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
   private final Map<Integer, List<MonsterDropEntry>> drops = new HashMap<>();
   private final List<MonsterGlobalDropEntry> globalDrops = new ArrayList<>();
   private final Map<Integer, List<MonsterGlobalDropEntry>> continentDrops = new HashMap<>();
   private final Map<Integer, List<Integer>> dropsChancePool = new HashMap<>();
   private final Set<Integer> hasNoMultiEquipDrops = new HashSet<>();
   private final Map<Integer, List<MonsterDropEntry>> extraMultiEquipDrops = new HashMap<>();
   private final Map<Pair<Integer, Integer>, Integer> mobAttackAnimationTime = new HashMap<>();
   private final Map<MobSkill, Integer> mobSkillAnimationTime = new HashMap<>();
   private final Map<Integer, Pair<Integer, Integer>> mobAttackInfo = new HashMap<>();
   private final Map<Integer, Boolean> mobBossCache = new HashMap<>();
   private final Map<Integer, String> mobNameCache = new HashMap<>();

   protected MapleMonsterInformationProvider() {
      retrieveGlobal();
   }

   public static MapleMonsterInformationProvider getInstance() {
      return instance;
   }

   public static List<Pair<Integer, String>> getMobsIDsFromName(String search) {
      MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("wz/String.wz"));
      MapleData data = dataProvider.getData("Mob.img");

      return data.getChildren().stream()
            .map(mapleData -> new Pair<>(Integer.parseInt(mapleData.getName()), MapleDataTool.getString(mapleData.getChildByPath("name"), "NO-NAME")))
            .filter(pair -> pair.getRight().toLowerCase().contains(search.toLowerCase()))
            .collect(Collectors.toList());
   }

   public final List<MonsterGlobalDropEntry> getRelevantGlobalDrops(int mapId) {
      int continentId = mapId / 100000000;

      List<MonsterGlobalDropEntry> continentItems = continentDrops.get(continentId);
      if (continentItems == null) {   // continent separated global drops found thanks to marcuswoon
         continentItems = globalDrops.stream()
               .filter(entry -> entry.continentid < 0 || entry.continentid == continentId)
               .collect(Collectors.toList());
         continentDrops.put(continentId, continentItems);
      }

      return continentItems;
   }

   private void retrieveGlobal() {
      DatabaseConnection.withConnection(connection -> globalDrops.addAll(DropDataProvider.getInstance().getGlobalDropData(connection)));
   }

   public List<MonsterDropEntry> retrieveEffectiveDrop(final int monsterId) {
      // this reads the drop entries searching for multi-equip, properly processing them

      List<MonsterDropEntry> list = retrieveDrop(monsterId);
      if (hasNoMultiEquipDrops.contains(monsterId) || !ServerConstants.USE_MULTIPLE_SAME_EQUIP_DROP) {
         return list;
      }

      List<MonsterDropEntry> multiDrops = extraMultiEquipDrops.get(monsterId), extra = new LinkedList<>();
      if (multiDrops == null) {
         multiDrops = new LinkedList<>();

         for (MonsterDropEntry mde : list) {
            if (ItemConstants.isEquipment(mde.itemId) && mde.Maximum > 1) {
               multiDrops.add(mde);

               int rnd = Randomizer.rand(mde.Minimum, mde.Maximum);
               for (int i = 0; i < rnd - 1; i++) {
                  extra.add(mde);   // this passes copies of the equips' MDE with min/max quantity > 1, but idc it'll be unused anyways
               }
            }
         }

         if (!multiDrops.isEmpty()) {
            extraMultiEquipDrops.put(monsterId, multiDrops);
         } else {
            hasNoMultiEquipDrops.add(monsterId);
         }
      } else {
         for (MonsterDropEntry mde : multiDrops) {
            int rnd = Randomizer.rand(mde.Minimum, mde.Maximum);
            for (int i = 0; i < rnd - 1; i++) {
               extra.add(mde);
            }
         }
      }

      List<MonsterDropEntry> ret = new LinkedList<>(list);
      ret.addAll(extra);

      return ret;
   }

   public final List<MonsterDropEntry> retrieveDrop(final int monsterId) {
      if (drops.containsKey(monsterId)) {
         return drops.get(monsterId);
      }
      List<MonsterDropEntry> ret = DatabaseConnection.withConnectionResult(connection ->
            DropDataProvider.getInstance().getDropDataForMonster(connection, monsterId)).orElse(new ArrayList<>());
      drops.put(monsterId, ret);
      return ret;
   }

   public final List<Integer> retrieveDropPool(final int monsterId) {  // ignores Quest and Party Quest items
      if (dropsChancePool.containsKey(monsterId)) {
         return dropsChancePool.get(monsterId);
      }

      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

      List<MonsterDropEntry> dropList = retrieveDrop(monsterId);
      List<Integer> ret = new ArrayList<>();

      int accProp = 0;
      for (MonsterDropEntry mde : dropList) {
         if (!ii.isQuestItem(mde.itemId) && !ii.isPartyQuestItem(mde.itemId)) {
            accProp += mde.chance;
         }

         ret.add(accProp);
      }

      if (accProp == 0) {
         ret.clear();    // don't accept mobs dropping no relevant items
      }
      dropsChancePool.put(monsterId, ret);
      return ret;
   }

   public final void setMobAttackAnimationTime(int monsterId, int attackPos, int animationTime) {
      mobAttackAnimationTime.put(new Pair<>(monsterId, attackPos), animationTime);
   }

   public final Integer getMobAttackAnimationTime(int monsterId, int attackPos) {
      Integer time = mobAttackAnimationTime.get(new Pair<>(monsterId, attackPos));
      return time == null ? 0 : time;
   }

   public final void setMobSkillAnimationTime(MobSkill skill, int animationTime) {
      mobSkillAnimationTime.put(skill, animationTime);
   }

   public final Integer getMobSkillAnimationTime(MobSkill skill) {
      Integer time = mobSkillAnimationTime.get(skill);
      return time == null ? 0 : time;
   }

   public final void setMobAttackInfo(int monsterId, int attackPos, int mpCon, int coolTime) {
      mobAttackInfo.put((monsterId << 3) + attackPos, new Pair<>(mpCon, coolTime));
   }

   public final Pair<Integer, Integer> getMobAttackInfo(int monsterId, int attackPos) {
      if (attackPos < 0 || attackPos > 7) {
         return null;
      }
      return mobAttackInfo.get((monsterId << 3) + attackPos);
   }

   public boolean isBoss(int id) {
      Boolean boss = mobBossCache.get(id);
      if (boss == null) {
         try {
            boss = MapleLifeFactory.getMonster(id).isBoss();
         } catch (NullPointerException npe) {
            boss = false;
         } catch (Exception e) {   //nonexistant mob
            boss = false;

            e.printStackTrace();
            System.err.println("Nonexistant mob id " + id);
         }

         mobBossCache.put(id, boss);
      }

      return boss;
   }

   public String getMobNameFromId(int id) {
      String mobName = mobNameCache.get(id);
      if (mobName == null) {
         try {
            mobName = MapleLifeFactory.getMonster(id).getName();
         } catch (NullPointerException npe) {
            mobName = ""; //nonexistant mob
         } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Nonexistant mob id " + id);
            mobName = ""; //nonexistant mob
         }

         mobNameCache.put(id, mobName);
      }

      return mobName;
   }

   public final void clearDrops() {
      drops.clear();
      hasNoMultiEquipDrops.clear();
      extraMultiEquipDrops.clear();
      dropsChancePool.clear();
      globalDrops.clear();
      continentDrops.clear();
      retrieveGlobal();
   }
}
