/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

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
package net.server.channel.handlers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleJob;
import client.Skill;
import client.SkillFactory;
import client.autoban.AutobanFactory;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.ServerConstants;
import constants.skills.Aran;
import constants.skills.Assassin;
import constants.skills.Bandit;
import constants.skills.Bishop;
import constants.skills.Bowmaster;
import constants.skills.ChiefBandit;
import constants.skills.Cleric;
import constants.skills.Corsair;
import constants.skills.Crossbowman;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.Fighter;
import constants.skills.Hero;
import constants.skills.Hunter;
import constants.skills.ILArchMage;
import constants.skills.Marauder;
import constants.skills.Marksman;
import constants.skills.NightWalker;
import constants.skills.Outlaw;
import constants.skills.Page;
import constants.skills.Paladin;
import constants.skills.Ranger;
import constants.skills.Rogue;
import constants.skills.Shadower;
import constants.skills.Sniper;
import constants.skills.Spearman;
import constants.skills.SuperGM;
import constants.skills.ThunderBreaker;
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import net.server.AbstractPacketHandler;
import net.server.MaplePacket;
import net.server.channel.packet.AttackPacket;
import scripting.AbstractPlayerInteraction;
import server.MapleStatEffect;
import server.TimerManager;
import server.life.Element;
import server.life.ElementalEffectiveness;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.life.MonsterDropEntry;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.Randomizer;
import tools.ServerNoticeType;
import tools.packet.monster.DamageMonster;
import tools.packet.remove.RemoveItem;
import tools.packet.stat.EnableActions;

public abstract class AbstractDealDamageHandler<T extends MaplePacket> extends AbstractPacketHandler<T> {
   // TODO move this
   public static MapleStatEffect getAttackEffect(AttackPacket attackInfo, MapleCharacter chr, Skill theSkill) {
      Skill mySkill = theSkill;
      if (mySkill == null) {
         mySkill = SkillFactory.getSkill(GameConstants.getHiddenSkill(attackInfo.skill())).orElseThrow();
      }

      int skillLevel = chr.getSkillLevel(mySkill);
      if (skillLevel == 0 && GameConstants.isPqSkillMap(chr.getMapId()) && GameConstants.isPqSkill(mySkill.getId())) {
         skillLevel = 1;
      }

      if (skillLevel == 0) {
         return null;
      }
      if (attackInfo.display() > 80) { //Hmm
         if (!mySkill.getAction()) {
            AutobanFactory.FAST_ATTACK.autoban(chr, "WZ Edit; adding action to a skill: " + attackInfo.display());
            return null;
         }
      }
      return mySkill.getEffect(skillLevel);
   }

   private void damageMonsterWithSkill(final MapleCharacter attacker, final MapleMap map, final MapleMonster monster, final int damage, int skillid, int fixedTime) {
      int animationTime;

      if (fixedTime == 0) {
         animationTime = SkillFactory.getSkill(skillid).map(Skill::getAnimationTime).orElse(0);
      } else {
         animationTime = fixedTime;
      }

      if (animationTime > 0) { // be sure to only use LIMITED ATTACKS with animation time here
         TimerManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
               MasterBroadcaster.getInstance().sendToAllInMapRange(map, new DamageMonster(monster.getObjectId(), damage), monster.getPosition());
               map.damageMonster(attacker, monster, damage);
            }
         }, animationTime);
      } else {
         MasterBroadcaster.getInstance().sendToAllInMapRange(map, new DamageMonster(monster.getObjectId(), damage), monster.getPosition());
         map.damageMonster(attacker, monster, damage);
      }
   }

   protected void applyAttack(AttackPacket attack, final MapleCharacter player, int attackCount) {
      final MapleMap map = player.getMap();
      if (map.isOwnershipRestricted(player)) {
         return;
      }

      Skill theSkill = null;
      MapleStatEffect attackEffect = null;
      final int job = player.getJob().getId();
      try {
         if (player.isBanned()) {
            return;
         }
         if (attack.skill() != 0) {
            theSkill = SkillFactory.getSkill(GameConstants.getHiddenSkill(attack.skill())).orElseThrow(); //returns back the skill id if its not a hidden skill so we are gucci
            attackEffect = getAttackEffect(attack, player, theSkill);
            if (attackEffect == null) {
               PacketCreator.announce(player, new EnableActions());
               return;
            }

            if (player.getMp() < attackEffect.getMpCon()) {
               AutobanFactory.MPCON.addPoint(player.getAutobanManager(), "Skill: " + attack.skill() + "; Player MP: " + player.getMp() + "; MP Needed: " + attackEffect.getMpCon());
            }

            int mobCount = attackEffect.getMobCount();
            if (attack.skill() != Cleric.HEAL) {
               if (player.isAlive()) {
                  if (attack.skill() == NightWalker.POISON_BOMB) {// Poison Bomb
                     attackEffect.applyTo(player, new Point(attack.position().x, attack.position().y));
                  } else if (attack.skill() != Aran.BODY_PRESSURE) {// prevent BP refreshing
                     attackEffect.applyTo(player);

                     if (attack.skill() == DawnWarrior.FINAL_ATTACK || attack.skill() == Page.FINAL_ATTACK_BW || attack.skill() == Page.FINAL_ATTACK_SWORD || attack.skill() == Fighter.FINAL_ATTACK_SWORD
                           || attack.skill() == Fighter.FINAL_ATTACK_AXE || attack.skill() == Spearman.FINAL_ATTACK_SPEAR || attack.skill() == Spearman.FINAL_ATTACK_POLEARM || attack.skill() == WindArcher.FINAL_ATTACK
                           || attack.skill() == DawnWarrior.FINAL_ATTACK || attack.skill() == Hunter.FINAL_ATTACK || attack.skill() == Crossbowman.FINAL_ATTACK) {

                        mobCount = 15;//:(
                     } else if (attack.skill() == Aran.HIDDEN_FULL_DOUBLE || attack.skill() == Aran.HIDDEN_FULL_TRIPLE || attack.skill() == Aran.HIDDEN_OVER_DOUBLE || attack.skill() == Aran.HIDDEN_OVER_TRIPLE) {
                        mobCount = 12;
                     }
                  }
               } else {
                  PacketCreator.announce(player, new EnableActions());
               }
            }

            if (attack.numAttacked() > mobCount) {
               AutobanFactory.MOB_COUNT.autoban(player, "Skill: " + attack.skill() + "; Count: " + attack.numAttacked() + " Max: " + attackEffect.getMobCount());
               return;
            }
         }
         if (!player.isAlive()) {
            return;
         }

         //WTF IS THIS F3,1
            /*if (attackCount != attack.numDamage && attack.skill() != ChiefBandit.MESO_EXPLOSION && attack.skill() != NightWalker.VAMPIRE && attack.skill() != WindArcher.WIND_SHOT && attack.skill() != Aran.COMBO_SMASH && attack.skill() != Aran.COMBO_FENRIR && attack.skill() != Aran.COMBO_TEMPEST && attack.skill() != NightLord.NINJA_AMBUSH && attack.skill() != Shadower.NINJA_AMBUSH) {
                return;
            }*/

         int totDamage = 0;

         if (attack.skill() == ChiefBandit.MESO_EXPLOSION) {
            int delay = 0;

            for (Integer oned : attack.getDamage().keySet()) {
               MapleMapObject mapobject = map.getMapObject(oned);
               if (mapobject != null && mapobject.getType() == MapleMapObjectType.ITEM) {
                  final MapleMapItem mapitem = (MapleMapItem) mapobject;
                  if (mapitem.getMeso() == 0) { //Maybe it is possible some how?
                     return;
                  }

                  mapitem.lockItem();
                  try {
                     if (mapitem.isPickedUp()) {
                        return;
                     }
                     TimerManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                           mapitem.lockItem();
                           try {
                              if (mapitem.isPickedUp()) {
                                 return;
                              }
                              map.pickItemDrop(PacketCreator.create(new RemoveItem(mapitem.getObjectId(), 4, 0)), mapitem);
                           } finally {
                              mapitem.unlockItem();
                           }
                        }
                     }, delay);
                     delay += 100;
                  } finally {
                     mapitem.unlockItem();
                  }
               } else if (mapobject != null && mapobject.getType() != MapleMapObjectType.MONSTER) {
                  return;
               }
            }
         }
         for (Integer oned : attack.getDamage().keySet()) {
            final MapleMonster monster = map.getMonsterByOid(oned);
            if (monster != null) {
               double distance = player.getPosition().distanceSq(monster.getPosition());
               double distanceToDetect = 200000.0;

               if (attack.ranged()) {
                  distanceToDetect += 400000;
               }

               if (attack.magic()) {
                  distanceToDetect += 200000;
               }

               if (player.getJob().isA(MapleJob.ARAN1)) {
                  distanceToDetect += 200000; // Arans have extra range over normal warriors.
               }

               if (attack.skill() == Aran.COMBO_SMASH || attack.skill() == Aran.BODY_PRESSURE) {
                  distanceToDetect += 40000;
               } else if (attack.skill() == Bishop.GENESIS || attack.skill() == ILArchMage.BLIZZARD || attack.skill() == FPArchMage.METEOR_SHOWER) {
                  distanceToDetect += 275000;
               } else if (attack.skill() == Hero.BRANDISH || attack.skill() == DragonKnight.SPEAR_CRUSHER || attack.skill() == DragonKnight.POLE_ARM_CRUSHER) {
                  distanceToDetect += 40000;
               } else if (attack.skill() == DragonKnight.DRAGON_ROAR || attack.skill() == SuperGM.SUPER_DRAGON_ROAR) {
                  distanceToDetect += 250000;
               } else if (attack.skill() == Shadower.BOOMERANG_STEP) {
                  distanceToDetect += 60000;
               }

               if (distance > distanceToDetect) {
                  AutobanFactory.DISTANCE_HACK.alert(player, "Distance Sq to monster: " + distance + " SID: " + attack.skill() + " MID: " + monster.getId());
                  monster.refreshMobPosition();
               }

               int totDamageToOneMonster = 0;
               List<Integer> onedList = attack.getDamage().get(oned);

               if (attack.magic()) { // thanks BHB, Alex (CanIGetaPR) for noticing no immunity status check here
                  if (monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) {
                     Collections.fill(onedList, 1);
                  }
               } else {
                  if (monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) {
                     Collections.fill(onedList, 1);
                  }
               }

               for (Integer eachd : onedList) {
                  if (eachd < 0) {
                     eachd += Integer.MAX_VALUE;
                  }
                  totDamageToOneMonster += eachd;
               }
               totDamage += totDamageToOneMonster;
               monster.aggroMonsterDamage(player, totDamageToOneMonster);
               if (player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null && (attack.skill() == 0 || attack.skill() == Rogue.DOUBLE_STAB || attack.skill() == Bandit.SAVAGE_BLOW || attack.skill() == ChiefBandit.ASSAULTER || attack.skill() == ChiefBandit.BAND_OF_THIEVES || attack.skill() == Shadower.ASSASSINATE || attack.skill() == Shadower.TAUNT || attack.skill() == Shadower.BOOMERANG_STEP)) {
                  Skill pickpocket = SkillFactory.getSkill(ChiefBandit.PICKPOCKET).orElseThrow();
                  int picklv = (player.isGM()) ? pickpocket.getMaxLevel() : player.getSkillLevel(pickpocket);
                  if (picklv > 0) {
                     int delay = 0;
                     final int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET);
                     for (Integer eachd : onedList) {
                        eachd += Integer.MAX_VALUE;

                        if (pickpocket.getEffect(picklv).makeChanceResult()) {
                           final Integer eachdf;
                           if (eachd < 0) {
                              eachdf = eachd + Integer.MAX_VALUE;
                           } else {
                              eachdf = eachd;
                           }

                           TimerManager.getInstance().schedule(new Runnable() {
                              @Override
                              public void run() {
                                 map.spawnMesoDrop(Math.min((int) Math.max(((double) eachdf / (double) 20000) * (double) maxmeso, 1), maxmeso), new Point((int) (monster.getPosition().getX() + Randomizer.nextInt(100) - 50), (int) (monster.getPosition().getY())), monster, player, true, (byte) 2);
                              }
                           }, delay);
                           delay += 100;
                        }
                     }
                  }
               } else if (attack.skill() == Marauder.ENERGY_DRAIN || attack.skill() == ThunderBreaker.ENERGY_DRAIN || attack.skill() == NightWalker.VAMPIRE || attack.skill() == Assassin.DRAIN) {
                  Skill attackSkill = SkillFactory.getSkill(attack.skill()).orElseThrow();
                  player.addHP(Math.min(monster.getMaxHp(), Math.min((int) ((double) totDamage * (double) attackSkill.getEffect(player.getSkillLevel(attackSkill)).getX() / 100.0), player.getCurrentMaxHp() / 2)));
               } else if (attack.skill() == Bandit.STEAL) {
                  Skill steal = SkillFactory.getSkill(Bandit.STEAL).orElseThrow();
                  if (monster.getStolen().size() < 1) { // One steal per mob <3
                     if (steal.getEffect(player.getSkillLevel(steal)).makeChanceResult()) {
                        monster.addStolen(0);
                        MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
                        List<Integer> dropPool = mi.retrieveDropPool(monster.getId());
                        if (!dropPool.isEmpty()) {
                           Integer rndPool = (int) Math.floor(Math.random() * dropPool.get(dropPool.size() - 1));

                           int i = 0;
                           while (rndPool >= dropPool.get(i)) i++;

                           List<MonsterDropEntry> toSteal = new ArrayList<>();
                           toSteal.add(mi.retrieveDrop(monster.getId()).get(i));

                           map.dropItemsFromMonster(toSteal, player, monster);
                           monster.addStolen(toSteal.get(0).itemId());
                        }
                     }
                  }
               } else if (attack.skill() == FPArchMage.FIRE_DEMON) {
                  SkillFactory.getSkill(attack.skill())
                        .map(skill -> skill.getEffect(player.getSkillLevel(skill)))
                        .ifPresent(effect -> monster.setTempEffectiveness(Element.ICE, ElementalEffectiveness.WEAK, effect.getDuration() * 1000));
               } else if (attack.skill() == ILArchMage.ICE_DEMON) {
                  SkillFactory.getSkill(attack.skill())
                        .map(skill -> skill.getEffect(player.getSkillLevel(skill)))
                        .ifPresent(effect -> monster.setTempEffectiveness(Element.FIRE, ElementalEffectiveness.WEAK, effect.getDuration() * 1000));
               } else if (attack.skill() == Outlaw.HOMING_BEACON || attack.skill() == Corsair.BULLSEYE) {
                  SkillFactory.getSkill(attack.skill())
                        .map(skill -> skill.getEffect(player.getSkillLevel(skill)))
                        .ifPresent(effect -> effect.applyBeaconBuff(player, monster.getObjectId()));
               } else if (attack.skill() == Outlaw.FLAME_THROWER) {
                  if (!monster.isBoss()) {
                     SkillFactory.executeIfHasSkill(player, attack.skill(), (skill, skillLevel) -> {
                        MapleStatEffect DoT = skill.getEffect(skillLevel);
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), skill, null, false);
                        monster.applyStatus(player, monsterStatusEffect, true, DoT.getDuration(), false);
                     });
                  }
               }

               if (player.isAran()) {
                  if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
                     if (totDamageToOneMonster > 0) {
                        SkillFactory.getSkill(Aran.SNOW_CHARGE).ifPresent(skill -> {
                           int skillLevel = player.getSkillLevel(skill);
                           MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.SPEED, skill.getEffect(skillLevel).getX()), skill, null, false);
                           monster.applyStatus(player, monsterStatusEffect, false, skill.getEffect(skillLevel).getY() * 1000);
                        });
                     }
                  }
               }
               if (player.getBuffedValue(MapleBuffStat.HAMSTRING) != null) {
                  SkillFactory.getSkill(Bowmaster.HAMSTRING).ifPresent(skill -> {
                     int skillLevel = player.getSkillLevel(skill);
                     MapleStatEffect effect = skill.getEffect(skillLevel);

                     if (effect.makeChanceResult()) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.SPEED, effect.getX()), skill, null, false);
                        monster.applyStatus(player, monsterStatusEffect, false, effect.getY() * 1000);
                     }
                  });
               }
               if (player.getBuffedValue(MapleBuffStat.SLOW) != null) {
                  SkillFactory.getSkill(Evan.SLOW).ifPresent(skill -> {
                     int skillLevel = player.getSkillLevel(skill);
                     MapleStatEffect effect = skill.getEffect(skillLevel);

                     if (effect.makeChanceResult()) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.SPEED, effect.getX()), skill, null, false);
                        monster.applyStatus(player, monsterStatusEffect, false, effect.getY() * 60 * 1000);
                     }
                  });
               }
               if (player.getBuffedValue(MapleBuffStat.BLIND) != null) {
                  SkillFactory.getSkill(Marksman.BLIND).ifPresent(skill -> {
                     int skillLevel = player.getSkillLevel(skill);
                     MapleStatEffect effect = skill.getEffect(skillLevel);

                     if (effect.makeChanceResult()) {
                        MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.ACC, effect.getX()), skill, null, false);
                        monster.applyStatus(player, monsterStatusEffect, false, effect.getY() * 1000);
                     }
                  });
               }
               if (job == 121 || job == 122) {
                  for (int charge = 1211005; charge < 1211007; charge++) {
                     Skill chargeSkill = SkillFactory.getSkill(charge).orElseThrow();
                     if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, chargeSkill)) {
                        if (totDamageToOneMonster > 0) {
                           if (charge == WhiteKnight.BW_ICE_CHARGE || charge == WhiteKnight.SWORD_ICE_CHARGE) {
                              monster.setTempEffectiveness(Element.ICE, ElementalEffectiveness.WEAK, chargeSkill.getEffect(player.getSkillLevel(chargeSkill)).getY() * 1000);
                              break;
                           }
                           if (charge == WhiteKnight.BW_FIRE_CHARGE || charge == WhiteKnight.SWORD_FIRE_CHARGE) {
                              monster.setTempEffectiveness(Element.FIRE, ElementalEffectiveness.WEAK, chargeSkill.getEffect(player.getSkillLevel(chargeSkill)).getY() * 1000);
                              break;
                           }
                        }
                     }
                  }
                  if (job == 122) {
                     for (int charge = 1221003; charge < 1221004; charge++) {
                        Skill chargeSkill = SkillFactory.getSkill(charge).orElseThrow();
                        if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, chargeSkill)) {
                           if (totDamageToOneMonster > 0) {
                              monster.setTempEffectiveness(Element.HOLY, ElementalEffectiveness.WEAK, chargeSkill.getEffect(player.getSkillLevel(chargeSkill)).getY() * 1000);
                              break;
                           }
                        }
                     }
                  }
               } else if (player.getBuffedValue(MapleBuffStat.COMBO_DRAIN) != null) {
                  Skill skill = SkillFactory.getSkill(21100005).orElseThrow();
                  player.addHP(((totDamage * skill.getEffect(player.getSkillLevel(skill)).getX()) / 100));
               } else if (job == 412 || job == 422 || job == 1411) {
                  Skill type = SkillFactory.getSkill(player.getJob().getId() == 412 ? 4120005 : (player.getJob().getId() == 1411 ? 14110004 : 4220005)).orElseThrow();
                  if (player.getSkillLevel(type) > 0) {
                     MapleStatEffect venomEffect = type.getEffect(player.getSkillLevel(type));
                     for (int i = 0; i < attackCount; i++) {
                        if (venomEffect.makeChanceResult()) {
                           if (monster.getVenomMulti() < 3) {
                              monster.setVenomMulti((monster.getVenomMulti() + 1));
                              MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON, 1), type, null, false);
                              monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                           }
                        }
                     }
                  }
               } else if (job >= 311 && job <= 322) {
                  if (!monster.isBoss()) {
                     Skill mortalBlow;
                     if (job == 311 || job == 312) {
                        mortalBlow = SkillFactory.getSkill(Ranger.MORTAL_BLOW).orElseThrow();
                     } else {
                        mortalBlow = SkillFactory.getSkill(Sniper.MORTAL_BLOW).orElseThrow();
                     }

                     int skillLevel = player.getSkillLevel(mortalBlow);
                     if (skillLevel > 0) {
                        MapleStatEffect mortal = mortalBlow.getEffect(skillLevel);
                        if (monster.getHp() <= (monster.getStats().hp() * mortal.getX()) / 100) {
                           if (Randomizer.rand(1, 100) <= mortal.getY()) {
                              map.damageMonster(player, monster, Integer.MAX_VALUE);  // thanks Conrad for noticing reduced EXP gain from skill kill
                           }
                        }
                     }
                  }
               }
               if (attack.skill() != 0) {
                  if (attackEffect.getFixDamage() != -1) {
                     if (totDamageToOneMonster != attackEffect.getFixDamage() && totDamageToOneMonster != 0) {
                        AutobanFactory.FIX_DAMAGE.autoban(player, totDamageToOneMonster + " damage");
                     }

                     int threeSnailsId = player.getJobType() * 10000000 + 1000;
                     if (attack.skill() == threeSnailsId) {
                        if (ServerConstants.USE_ULTRA_THREE_SNAILS) {
                           int skillLv = player.getSkillLevel(threeSnailsId);

                           if (skillLv > 0) {
                              AbstractPlayerInteraction api = player.getAbstractPlayerInteraction();

                              int shellId;
                              switch (skillLv) {
                                 case 1:
                                    shellId = 4000019;
                                    break;

                                 case 2:
                                    shellId = 4000000;
                                    break;

                                 default:
                                    shellId = 4000016;
                              }

                              if (api.haveItem(shellId, 1)) {
                                 api.gainItem(shellId, (short) -1, false);
                                 totDamageToOneMonster *= player.getLevel();
                              } else {
                                 MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "You have ran out of shells to activate the hidden power of Three Snails.");
                              }
                           } else {
                              totDamageToOneMonster = 0;
                           }
                        }
                     }
                  }
               }
               if (totDamageToOneMonster > 0 && attackEffect != null) {
                  Map<MonsterStatus, Integer> attackEffectStati = attackEffect.getMonsterStati();
                  if (!attackEffectStati.isEmpty()) {
                     if (attackEffect.makeChanceResult()) {
                        monster.applyStatus(player, new MonsterStatusEffect(attackEffectStati, theSkill, null, false), attackEffect.isPoison(), attackEffect.getDuration());
                     }
                  }
               }
               if (attack.skill() == Paladin.HEAVENS_HAMMER) {
                  if (!monster.isBoss()) {
                     damageMonsterWithSkill(player, map, monster, monster.getHp() - 1, attack.skill(), 1777);
                  } else {
                     int skillDamage = SkillFactory.getSkill(Paladin.HEAVENS_HAMMER)
                           .map(skill -> skill.getEffect(player.getSkillLevel(skill)))
                           .map(MapleStatEffect::getDamage)
                           .orElse(0);
                     int HHDmg = (player.calculateMaxBaseDamage(player.getTotalWatk()) * (skillDamage / 100));
                     damageMonsterWithSkill(player, map, monster, (int) (Math.floor(Math.random() * (HHDmg / 5) + HHDmg * .8)), attack.skill(), 1777);
                  }
               } else if (attack.skill() == Aran.COMBO_TEMPEST) {
                  if (!monster.isBoss()) {
                     damageMonsterWithSkill(player, map, monster, monster.getHp(), attack.skill(), 0);
                  } else {
                     int skillDamage = SkillFactory.getSkill(Aran.COMBO_TEMPEST)
                           .map(skill -> skill.getEffect(player.getSkillLevel(skill)))
                           .map(MapleStatEffect::getDamage).orElse(0);
                     int TmpDmg = (player.calculateMaxBaseDamage(player.getTotalWatk()) * (skillDamage / 100));
                     damageMonsterWithSkill(player, map, monster, (int) (Math.floor(Math.random() * (TmpDmg / 5) + TmpDmg * .8)), attack.skill(), 0);
                  }
               } else {
                  if (attack.skill() == Aran.BODY_PRESSURE) {
                     int finalTotDamageToOneMonster = totDamageToOneMonster;
                     MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageMonster(monster.getObjectId(), finalTotDamageToOneMonster));
                  }

                  map.damageMonster(player, monster, totDamageToOneMonster);
               }
               if (monster.isBuffed(MonsterStatus.WEAPON_REFLECT) && !attack.magic()) {
                  List<Pair<Integer, Integer>> mobSkills = monster.getSkills();

                  for (Pair<Integer, Integer> ms : mobSkills) {
                     if (ms.left == 145) {
                        MobSkill toUse = MobSkillFactory.getMobSkill(ms.left, ms.right);
                        player.addHP(-toUse.getX());
                        MasterBroadcaster.getInstance().sendToAllInMap(map, character -> MaplePacketCreator.damagePlayer(0, monster.getId(), player.getId(), toUse.getX(), 0, 0, false, 0, true, monster.getObjectId(), 0, 0), true, player);
                     }
                  }
               }
               if (monster.isBuffed(MonsterStatus.MAGIC_REFLECT) && attack.magic()) {
                  List<Pair<Integer, Integer>> mobSkills = monster.getSkills();

                  for (Pair<Integer, Integer> ms : mobSkills) {
                     if (ms.left == 145) {
                        MobSkill toUse = MobSkillFactory.getMobSkill(ms.left, ms.right);
                        player.addHP(-toUse.getY());
                        MasterBroadcaster.getInstance().sendToAllInMap(map, character -> MaplePacketCreator.damagePlayer(0, monster.getId(), player.getId(), toUse.getY(), 0, 0, false, 0, true, monster.getObjectId(), 0, 0), true, player);
                     }
                  }
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
