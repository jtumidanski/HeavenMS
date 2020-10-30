package server.processor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleAbnormalStatus;
import client.MapleCharacter;
import client.status.MonsterStatus;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import constants.game.GameConstants;
import net.server.services.task.channel.OverallService;
import net.server.services.type.ChannelServices;
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
      Runnable toRun = () -> {
         if (monster.isAlive()) {
            applyEffect(player, monster, mobSkill, skill, null);
         }
      };

      OverallService service = (OverallService) monster.getMap().getChannelServer().getServiceAccess(ChannelServices.OVERALL);
      service.registerOverallAction(monster.getMap().getId(), toRun, animationTime);
   }

   public void applyEffect(MapleCharacter player, MapleMonster monster, MobSkill mobSkill, boolean skill, List<MapleCharacter> banishPlayers) {
      MapleAbnormalStatus disease = null;
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
            if (mobSkill.lt() != null && mobSkill.rb() != null && skill) {
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
            disease = MapleAbnormalStatus.SEAL;
            break;
         case 121:
            disease = MapleAbnormalStatus.DARKNESS;
            break;
         case 122:
            disease = MapleAbnormalStatus.WEAKEN;
            break;
         case 123:
            disease = MapleAbnormalStatus.STUN;
            break;
         case 124:
            disease = MapleAbnormalStatus.CURSE;
            break;
         case 125:
            disease = MapleAbnormalStatus.POISON;
            break;
         case 126: // Slow
            disease = MapleAbnormalStatus.SLOW;
            break;
         case 127:
            if (mobSkill.lt() != null && mobSkill.rb() != null && skill) {
               for (MapleCharacter character : getPlayersInRange(mobSkill, monster)) {
                  character.dispel();
               }
            } else {
               player.dispel();
            }
            break;
         case 128: // Seduce
            disease = MapleAbnormalStatus.SEDUCE;
            break;
         case 129: // Banish
            if (mobSkill.lt() != null && mobSkill.rb() != null && skill) {
               banishPlayers.addAll(getPlayersInRange(mobSkill, monster));
            } else {
               banishPlayers.add(player);
            }
            break;
         case 131: // Mist
            monster.getMap().spawnMist(new MapleMist(calculateBoundingBox(mobSkill, monster.position()), monster, mobSkill), mobSkill.x() * 100, false, false, false);
            break;
         case 132:
            disease = MapleAbnormalStatus.CONFUSE;
            break;
         case 133: // zombify
            disease = MapleAbnormalStatus.ZOMBIFY;
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

            if (GameConstants.isDojo(map.getId())) {  // spawns in dojo should be unlimited
               skillLimit = Integer.MAX_VALUE;
            }

            if (map.getSpawnedMonstersOnMap() < 80) {
               List<Integer> summons = mobSkill.summons();
               int summonLimit = monster.countAvailableMobSummons(summons.size(), skillLimit);
               if (summonLimit >= 1) {
                  boolean bossRushMap = GameConstants.isBossRush(map.getId());

                  Collections.shuffle(summons);
                  for (Integer mobId : summons.subList(0, summonLimit)) {
                     MapleLifeFactory.getMonster(mobId).ifPresent(toSpawn -> {
                        if (bossRushMap) {
                           toSpawn.disableDrops();
                        }
                        toSpawn.setPosition(monster.position());
                        int yPosition, xPosition;
                        xPosition = (int) monster.position().getX();
                        yPosition = (int) monster.position().getY();
                        switch (mobId) {
                           case 8500003: // Pap bomb high
                              toSpawn.setFh((int) Math.ceil(Math.random() * 19.0));
                              yPosition = -590;
                              break;
                           case 8500004: // Pap bomb
                              xPosition = (int) (monster.position().getX() + Randomizer.nextInt(1000) - 500);
                              if (yPosition != -590) {
                                 yPosition = (int) monster.position().getY();
                              }
                              break;
                           case 8510100: //Pianus bomb
                              if (Math.ceil(Math.random() * 5) == 1) {
                                 yPosition = 78;
                                 xPosition = Randomizer.nextInt(5) + (Randomizer.nextInt(2) == 1 ? 180 : 0);
                              } else {
                                 xPosition = (int) (monster.position().getX() + Randomizer.nextInt(1000) - 500);
                              }
                              break;
                        }
                        switch (map.getId()) {
                           case 220080001: //Pap map
                              if (xPosition < -890) {
                                 xPosition = (int) (Math.ceil(Math.random() * 150) - 890);
                              } else if (xPosition > 230) {
                                 xPosition = (int) (230 - Math.ceil(Math.random() * 150));
                              }
                              break;
                           case 230040420: // Pianus map
                              if (xPosition < -239) {
                                 xPosition = (int) (Math.ceil(Math.random() * 150) - 239);
                              } else if (xPosition > 371) {
                                 xPosition = (int) (371 - Math.ceil(Math.random() * 150));
                              }
                              break;
                        }
                        toSpawn.setPosition(new Point(xPosition, yPosition));
                        if (toSpawn.id() == 8500004) {
                           map.spawnFakeMonster(toSpawn);
                        } else {
                           map.spawnMonsterWithEffect(toSpawn, mobSkill.spawnEffect(), toSpawn.position());
                        }
                        monster.addSummonedMob(toSpawn);
                     });
                  }
               }
            }
            break;
         default:
            LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Unhandled Mob skill: " + mobSkill.skillId());
            break;
      }
      if (stats.size() > 0) {
         if (mobSkill.lt() != null && mobSkill.rb() != null && skill) {
            for (MapleMapObject mons : getObjectsInRange(mobSkill, monster, MapleMapObjectType.MONSTER)) {
               ((MapleMonster) mons).applyMonsterBuff(stats, mobSkill.x(), mobSkill.skillId(), mobSkill.duration(), mobSkill, reflection);
            }
         } else {
            monster.applyMonsterBuff(stats, mobSkill.x(), mobSkill.skillId(), mobSkill.duration(), mobSkill, reflection);
         }
      }
      if (disease != null) {
         if (mobSkill.lt() != null && mobSkill.rb() != null && skill) {
            int i = 0;
            for (MapleCharacter character : getPlayersInRange(mobSkill, monster)) {
               if (!character.hasActiveBuff(2321005)) {  // holy shield
                  if (disease.equals(MapleAbnormalStatus.SEDUCE)) {
                     if (i < 10) {
                        character.giveAbnormalStatus(MapleAbnormalStatus.SEDUCE, mobSkill);
                        i++;
                     }
                  } else {
                     character.giveAbnormalStatus(disease, mobSkill);
                  }
               }
            }
         } else {
            player.giveAbnormalStatus(disease, mobSkill);
         }
      }
   }

   private List<MapleCharacter> getPlayersInRange(MobSkill mobSkill, MapleMonster monster) {
      return monster.getMap().getPlayersInRange(calculateBoundingBox(mobSkill, monster.position()));
   }

   private Rectangle calculateBoundingBox(MobSkill mobSkill, Point posFrom) {
      Point mylt = new Point(mobSkill.lt().x + posFrom.x, mobSkill.lt().y + posFrom.y);
      Point myrb = new Point(mobSkill.rb().x + posFrom.x, mobSkill.rb().y + posFrom.y);
      return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
   }

   private List<MapleMapObject> getObjectsInRange(MobSkill mobSkill, MapleMonster monster, MapleMapObjectType objectType) {
      return monster.getMap().getMapObjectsInBox(calculateBoundingBox(mobSkill, monster.position()), Collections.singletonList(objectType));
   }
}