package server.processor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacter;
import client.MapleDisease;
import client.status.MonsterStatus;
import constants.game.GameConstants;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import tools.ArrayMap;
import tools.Randomizer;

public class MobSkillProcessor {
   private static MobSkillProcessor instance;

   public static MobSkillProcessor getInstance() {
      if (instance == null) {
         instance = new MobSkillProcessor();
      }
      return instance;
   }

   private MobSkillProcessor() {
   }

   public void applyDelayedEffect(final MapleCharacter player, final MapleMonster monster, MobSkill mobSkill, final boolean skill, int animationTime) {
      Runnable toRun = new Runnable() {
         @Override
         public void run() {
            if (monster.isAlive()) {
               applyEffect(player, monster, mobSkill, skill, null);
            }
         }
      };

      monster.getMap().getChannelServer().registerOverallAction(monster.getMap().getId(), toRun, animationTime);
   }

   public void applyEffect(MapleCharacter player, MapleMonster monster, MobSkill mobSkill, boolean skill, List<MapleCharacter> banishPlayers) {
      MapleDisease disease = null;
      Map<MonsterStatus, Integer> stats = new ArrayMap<>();
      List<Integer> reflection = new LinkedList<>();
      switch (mobSkill.skillId()) {
         case 100:
         case 110:
         case 150:
            stats.put(MonsterStatus.WEAPON_ATTACK_UP, mobSkill.x());
            break;
         case 101:
         case 111:
         case 151:
            stats.put(MonsterStatus.MAGIC_ATTACK_UP, mobSkill.x());
            break;
         case 102:
         case 112:
         case 152:
            stats.put(MonsterStatus.WEAPON_DEFENSE_UP, mobSkill.x());
            break;
         case 103:
         case 113:
         case 153:
            stats.put(MonsterStatus.MAGIC_DEFENSE_UP, mobSkill.x());
            break;
         case 114:
            if (mobSkill.lt().isDefined() && mobSkill.rb().isDefined() && skill) {
               List<MapleMapObject> objects = getObjectsInRange(mobSkill, monster, MapleMapObjectType.MONSTER);
               final int hps = (mobSkill.x() / 1000) * (int) (950 + 1050 * Math.random());
               for (MapleMapObject mons : objects) {
                  ((MapleMonster) mons).heal(hps, mobSkill.y());
               }
            } else {
               monster.heal(mobSkill.x(), mobSkill.y());
            }
            break;
         case 120:
            disease = MapleDisease.SEAL;
            break;
         case 121:
            disease = MapleDisease.DARKNESS;
            break;
         case 122:
            disease = MapleDisease.WEAKEN;
            break;
         case 123:
            disease = MapleDisease.STUN;
            break;
         case 124:
            disease = MapleDisease.CURSE;
            break;
         case 125:
            disease = MapleDisease.POISON;
            break;
         case 126: // Slow
            disease = MapleDisease.SLOW;
            break;
         case 127:
            if (mobSkill.lt().isDefined() && mobSkill.rb().isDefined() && skill) {
               for (MapleCharacter character : getPlayersInRange(mobSkill, monster, player)) {
                  character.dispel();
               }
            } else {
               player.dispel();
            }
            break;
         case 128: // Seduce
            disease = MapleDisease.SEDUCE;
            break;
         case 129: // Banish
            if (mobSkill.lt().isDefined() && mobSkill.rb().isDefined() && skill) {
               banishPlayers.addAll(getPlayersInRange(mobSkill, monster, player));
            } else {
               banishPlayers.add(player);
            }
            break;
         case 131: // Mist
            monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(mobSkill, monster.position(), monster.isFacingLeft()), monster, mobSkill), mobSkill.x() * 100, false, false, false);
            break;
         case 132:
            disease = MapleDisease.CONFUSE;
            break;
         case 133: // zombify
            disease = MapleDisease.ZOMBIFY;
            break;
         case 140:
            if (mobSkill.makeChanceResult() && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) {
               stats.put(MonsterStatus.WEAPON_IMMUNITY, mobSkill.x());
            }
            break;
         case 141:
            if (mobSkill.makeChanceResult() && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) {
               stats.put(MonsterStatus.MAGIC_IMMUNITY, mobSkill.x());
            }
            break;
         case 143: // Weapon Reflect
            stats.put(MonsterStatus.WEAPON_REFLECT, 10);
            stats.put(MonsterStatus.WEAPON_IMMUNITY, 10);
            reflection.add(mobSkill.x());
            break;
         case 144: // Magic Reflect
            stats.put(MonsterStatus.MAGIC_REFLECT, 10);
            stats.put(MonsterStatus.MAGIC_IMMUNITY, 10);
            reflection.add(mobSkill.x());
            break;
         case 145: // Weapon / Magic reflect
            stats.put(MonsterStatus.WEAPON_REFLECT, 10);
            stats.put(MonsterStatus.WEAPON_IMMUNITY, 10);
            stats.put(MonsterStatus.MAGIC_REFLECT, 10);
            stats.put(MonsterStatus.MAGIC_IMMUNITY, 10);
            reflection.add(mobSkill.x());
            break;
         case 154:
            stats.put(MonsterStatus.ACC, mobSkill.x());
            break;
         case 155:
            stats.put(MonsterStatus.AVOID, mobSkill.x());
            break;
         case 156:
            stats.put(MonsterStatus.SPEED, mobSkill.x());
            break;
         case 200: // summon
            int skillLimit = mobSkill.limit();
            MapleMap map = monster.getMap();

            if (map.isDojoMap()) {  // spawns in dojo should be unlimited
               skillLimit = Integer.MAX_VALUE;
            }

            if (map.getSpawnedMonstersOnMap() < 80) {
               List<Integer> summons = mobSkill.summons();
               int summonLimit = monster.countAvailableMobSummons(summons.size(), skillLimit);
               if (summonLimit >= 1) {
                  boolean bossRushMap = GameConstants.isBossRush(map.getId());

                  Collections.shuffle(summons);
                  for (Integer mobId : summons.subList(0, summonLimit)) {
                     MapleMonster toSpawn = MapleLifeFactory.getMonster(mobId);
                     if (toSpawn != null) {
                        if (bossRushMap) {
                           toSpawn.disableDrops();  // no littering on BRPQ pls
                        }
                        toSpawn.position_$eq(monster.position());
                        int ypos, xpos;
                        xpos = (int) monster.position().getX();
                        ypos = (int) monster.position().getY();
                        switch (mobId) {
                           case 8500003: // Pap bomb high
                              toSpawn.fh_$eq((int) Math.ceil(Math.random() * 19.0));
                              ypos = -590;
                              break;
                           case 8500004: // Pap bomb
                              xpos = (int) (monster.position().getX() + Randomizer.nextInt(1000) - 500);
                              if (ypos != -590) {
                                 ypos = (int) monster.position().getY();
                              }
                              break;
                           case 8510100: //Pianus bomb
                              if (Math.ceil(Math.random() * 5) == 1) {
                                 ypos = 78;
                                 xpos = Randomizer.nextInt(5) + (Randomizer.nextInt(2) == 1 ? 180 : 0);
                              } else {
                                 xpos = (int) (monster.position().getX() + Randomizer.nextInt(1000) - 500);
                              }
                              break;
                        }
                        switch (map.getId()) {
                           case 220080001: //Pap map
                              if (xpos < -890) {
                                 xpos = (int) (Math.ceil(Math.random() * 150) - 890);
                              } else if (xpos > 230) {
                                 xpos = (int) (230 - Math.ceil(Math.random() * 150));
                              }
                              break;
                           case 230040420: // Pianus map
                              if (xpos < -239) {
                                 xpos = (int) (Math.ceil(Math.random() * 150) - 239);
                              } else if (xpos > 371) {
                                 xpos = (int) (371 - Math.ceil(Math.random() * 150));
                              }
                              break;
                        }
                        toSpawn.position_$eq(new Point(xpos, ypos));
                        if (toSpawn.id() == 8500004) {
                           map.spawnFakeMonster(toSpawn);
                        } else {
                           map.spawnMonsterWithEffect(toSpawn, mobSkill.spawnEffect(), toSpawn.position());
                        }
                        monster.addSummonedMob(toSpawn);
                     }
                  }
               }
            }
            break;
         default:
            System.out.println("Unhandled Mob skill: " + mobSkill.skillId());
            break;
      }
      if (stats.size() > 0) {
         if (mobSkill.lt().isDefined() && mobSkill.rb().isDefined() && skill) {
            for (MapleMapObject mons : getObjectsInRange(mobSkill, monster, MapleMapObjectType.MONSTER)) {
               ((MapleMonster) mons).applyMonsterBuff(stats, mobSkill.x(), mobSkill.skillId(), mobSkill.duration(), mobSkill, reflection);
            }
         } else {
            monster.applyMonsterBuff(stats, mobSkill.x(), mobSkill.skillId(), mobSkill.duration(), mobSkill, reflection);
         }
      }
      if (disease != null) {
         if (mobSkill.lt().isDefined() && mobSkill.rb().isDefined() && skill) {
            int i = 0;
            for (MapleCharacter character : getPlayersInRange(mobSkill, monster, player)) {
               if (!character.hasActiveBuff(2321005)) {  // holy shield
                  if (disease.equals(MapleDisease.SEDUCE)) {
                     if (i < 10) {
                        character.giveDebuff(MapleDisease.SEDUCE, mobSkill);
                        i++;
                     }
                  } else {
                     character.giveDebuff(disease, mobSkill);
                  }
               }
            }
         } else {
            player.giveDebuff(disease, mobSkill);
         }
      }
   }

   private List<MapleCharacter> getPlayersInRange(MobSkill mobSkill, MapleMonster monster, MapleCharacter player) {
      return monster.getMap().getPlayersInRange(calculateBoundingBox(mobSkill, monster.position(), monster.isFacingLeft()), Collections.singletonList(player));
   }

   private Rectangle calculateBoundingBox(MobSkill mobSkill, Point posFrom, boolean facingLeft) {
      int multiplier = facingLeft ? 1 : -1;
      Point mylt = new Point(mobSkill.lt().get().x * multiplier + posFrom.x, mobSkill.lt().get().y + posFrom.y);
      Point myrb = new Point(mobSkill.rb().get().x * multiplier + posFrom.x, mobSkill.rb().get().y + posFrom.y);
      return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
   }

   private List<MapleMapObject> getObjectsInRange(MobSkill mobSkill, MapleMonster monster, MapleMapObjectType objectType) {
      return monster.getMap().getMapObjectsInBox(calculateBoundingBox(mobSkill, monster.position(), monster.isFacingLeft()), Collections.singletonList(objectType));
   }
}