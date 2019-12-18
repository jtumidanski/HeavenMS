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
import java.util.Optional;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.YamlConfig;
import constants.game.GameConstants;
import constants.inventory.ItemConstants;
import constants.skills.Aran;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.TakeDamagePacket;
import net.server.channel.packet.reader.TakeDamageReader;
import server.MapleStatEffect;
import server.life.LoseItem;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobAttackInfoFactory;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.processor.MobSkillProcessor;
import tools.FilePrinter;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.Randomizer;
import tools.packet.GetEnergy;
import tools.packet.character.DamageCharacter;
import tools.packet.foreigneffect.ShowBuffEffect;
import tools.packet.monster.DamageMonster;
import tools.packet.showitemgaininchat.ShowOwnBuffEffect;

public final class TakeDamageHandler extends AbstractPacketHandler<TakeDamagePacket> {
   @Override
   public Class<TakeDamageReader> getReaderClass() {
      return TakeDamageReader.class;
   }

   @Override
   public void handlePacket(TakeDamagePacket packet, MapleClient client) {
      List<MapleCharacter> banishPlayers = new ArrayList<>();

      MapleCharacter chr = client.getPlayer();
      int damagefrom = packet.damageFrom();
      int monsteridfrom = packet.monsterIdFrom();
      int damage = packet.damage();
      byte direction = packet.direction();

      int oid = 0, pgmr = 0;
      int pos_x = 0, pos_y = 0, fake = 0;
      boolean is_pgmr = false, is_pg = true, is_deadly = false;
      int mpattack = 0;
      MapleMonster attacker = null;
      final MapleMap map = chr.getMap();
      if (damagefrom != -3 && damagefrom != -4) {
         try {
            MapleMapObject mmo = map.getMapObject(oid);
            if (mmo instanceof MapleMonster) {
               attacker = (MapleMonster) mmo;
               if (attacker.id() != monsteridfrom) {
                  attacker = null;
               }
            }

            if (attacker != null) {
               if (attacker.isBuffed(MonsterStatus.NEUTRALISE)) {
                  return;
               }

               List<LoseItem> loseItems;
               if (damage > 0) {
                  loseItems = attacker.getStats().loseItem();
                  if (loseItems != null) {
                     if (chr.getBuffEffect(MapleBuffStat.AURA) == null) {
                        MapleInventoryType type;
                        final int playerpos = chr.position().x;
                        byte d = 1;
                        Point pos = new Point(0, chr.position().y);
                        for (LoseItem loseItem : loseItems) {
                           type = ItemConstants.getInventoryType(loseItem.id());

                           int dropCount = 0;
                           for (byte b = 0; b < loseItem.x(); b++) {
                              if (Randomizer.nextInt(100) < loseItem.chance()) {
                                 dropCount += 1;
                              }
                           }

                           if (dropCount > 0) {
                              int qty;

                              MapleInventory inv = chr.getInventory(type);
                              inv.lockInventory();
                              try {
                                 qty = Math.min(chr.countItem(loseItem.id()), dropCount);
                                 MapleInventoryManipulator.removeById(client, type, loseItem.id(), qty, false, false);
                              } finally {
                                 inv.unlockInventory();
                              }

                              if (loseItem.id() == 4031868) {
                                 chr.updateAriantScore();
                              }

                              for (byte b = 0; b < qty; b++) {
                                 pos.x = playerpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2)));
                                 map.spawnItemDrop(chr, chr, new Item(loseItem.id(), (short) 0, (short) 1), map.calcDropPos(pos, chr.position()), true, true);
                                 d++;
                              }
                           }
                        }
                     }
                     map.removeMapObject(attacker);
                  }
               }
            } else if (damagefrom != 0 || !map.removeSelfDestructive(oid)) {    // thanks inhyuk for noticing self-destruct damage not being handled properly
               return;
            }
         } catch (ClassCastException e) {
            //this happens due to mob on last map damaging player just before changing maps

            e.printStackTrace();
            FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, "Attacker is not a mob-type, rather is a " + map.getMapObject(oid).getClass().getName() + " entity.");

            return;
         }
      }
      if (damagefrom != -1 && damagefrom != -2 && attacker != null) {
         MobAttackInfo attackInfo = MobAttackInfoFactory.getMobAttackInfo(attacker, damagefrom);
         if (attackInfo != null) {
            if (attackInfo.deadlyAttack()) {
               mpattack = chr.getMp() - 1;
               is_deadly = true;
            }
            mpattack += attackInfo.mpBurn();
            MobSkill mobSkill = MobSkillFactory.getMobSkill(attackInfo.diseaseSkill(), attackInfo.diseaseLevel());
            if (mobSkill != null && damage > 0) {
               MobSkillProcessor.getInstance().applyEffect(chr, attacker, mobSkill, false, banishPlayers);
            }

            attacker.setMp(attacker.getMp() - attackInfo.mpCon());
            if (chr.getBuffedValue(MapleBuffStat.MANA_REFLECTION) != null && damage > 0 && !attacker.isBoss()) {
               int jobid = chr.getJob().getId();
               if (jobid == 212 || jobid == 222 || jobid == 232) {
                  int id = jobid * 10000 + 1002;
                  Optional<Skill> manaReflectSkill = SkillFactory.getSkill(id);
                  if (manaReflectSkill.isPresent()) {
                     Skill skill = manaReflectSkill.get();
                     int skillLevel = chr.getSkillLevel(skill);

                     if (chr.isBuffFrom(MapleBuffStat.MANA_REFLECTION, skill) && skillLevel > 0
                           && skill.getEffect(skillLevel).makeChanceResult()) {
                        int bouncedamage = (damage * manaReflectSkill.get().getEffect(skillLevel).getX() / 100);
                        if (bouncedamage > attacker.getMaxHp() / 5) {
                           bouncedamage = attacker.getMaxHp() / 5;
                        }
                        map.damageMonster(chr, attacker, bouncedamage);
                        int finalBouncedamage = bouncedamage;
                        MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageMonster(oid, finalBouncedamage), true, chr);
                        PacketCreator.announce(chr, new ShowOwnBuffEffect(id, 5));
                        MasterBroadcaster.getInstance().sendToAllInMap(map, new ShowBuffEffect(chr.getId(), id, 5, (byte) 3), false, chr);
                     }
                  }
               }
            }
         }
      }

      if (damage == -1) {
         fake = 4020002 + (chr.getJob().getId() / 10 - 40) * 100000;
      }

      if (damage > 0) {
         chr.getAutobanManager().resetMisses();
      } else {
         chr.getAutobanManager().addMiss();
      }

      //in dojo player cannot use pot, so deadly attacks should be turned off as well
      if (is_deadly && GameConstants.isDojo(chr.getMap().getId()) && !YamlConfig.config.server.USE_DEADLY_DOJO) {
         damage = 0;
         mpattack = 0;
      }

      if (damage > 0 && !chr.isHidden()) {
         if (attacker != null) {
            if (damagefrom == -1) {
               if (chr.getBuffedValue(MapleBuffStat.POWERGUARD) != null) { // PG works on bosses, but only at half of the rate.
                  int bouncedamage = (int) (damage * (chr.getBuffedValue(MapleBuffStat.POWERGUARD).doubleValue() / (attacker.isBoss() ? 200 : 100)));
                  bouncedamage = Math.min(bouncedamage, attacker.getMaxHp() / 10);
                  damage -= bouncedamage;
                  map.damageMonster(chr, attacker, bouncedamage);
                  int finalBouncedamage = bouncedamage;
                  MasterBroadcaster.getInstance().sendToAllInMapRange(map, new DamageMonster(oid, finalBouncedamage), false, chr, true);
                  attacker.aggroMonsterDamage(chr, bouncedamage);
               }
               MapleStatEffect bPressure = chr.getBuffEffect(MapleBuffStat.BODY_PRESSURE);
               if (bPressure != null) {
                  Optional<Skill> skill = SkillFactory.getSkill(Aran.BODY_PRESSURE);
                  if (skill.isPresent()) {
                     if (!attacker.alreadyBuffedStats().contains(MonsterStatus.NEUTRALISE)) {
                        if (!attacker.isBoss() && bPressure.makeChanceResult()) {
                           attacker.applyStatus(chr, new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.NEUTRALISE, 1), skill.get(), null, false), false, (bPressure.getDuration() / 10) * 2, false);
                        }
                     }
                  }
               }
            }

            MapleStatEffect cBarrier = chr.getBuffEffect(MapleBuffStat.COMBO_BARRIER);  // thanks BHB for noticing Combo Barrier buff not working
            if (cBarrier != null) {
               damage *= (cBarrier.getX() / 1000.0);
            }
         }
         if (damagefrom != -3 && damagefrom != -4) {
            int jobid = chr.getJob().getId();
            if (jobid < 200 && jobid % 10 == 2) {
               Optional<Skill> skill = SkillFactory.getSkill(jobid * 10000 + (jobid == 112 ? 4 : 5));
               if (skill.isPresent()) {
                  int skillLevel = chr.getSkillLevel(skill.get());
                  damage *= (skill.get().getEffect(skillLevel).getX() / 1000.0);
               }
            }

            Optional<Skill> highDef = SkillFactory.getSkill(Aran.HIGH_DEFENSE);
            if (highDef.isPresent()) {
               int hdLevel = chr.getSkillLevel(highDef.get());
               if (hdLevel > 0) {
                  damage *= Math.ceil(highDef.get().getEffect(hdLevel).getX() / 1000.0);
               }
            }
         }
         Integer mesoguard = chr.getBuffedValue(MapleBuffStat.MESOGUARD);
         if (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null && mpattack == 0) {
            int mploss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0));
            int hploss = damage - mploss;

            int curmp = chr.getMp();
            if (mploss > curmp) {
               hploss += mploss - curmp;
               mploss = curmp;
            }

            chr.addMPHP(-hploss, -mploss);
         } else if (mesoguard != null) {
            damage = Math.round(damage / 2);
            int mesoloss = (int) (damage * (mesoguard.doubleValue() / 100.0));
            if (chr.getMeso() < mesoloss) {
               chr.gainMeso(-chr.getMeso(), false);
               chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
            } else {
               chr.gainMeso(-mesoloss, false);
            }
            chr.addMPHP(-damage, -mpattack);
         } else {
            if (chr.isRidingBattleship()) {
               chr.decreaseBattleshipHp(damage);
            }
            chr.addMPHP(-damage, -mpattack);
         }
      }
      if (!chr.isHidden()) {
         MasterBroadcaster.getInstance().sendToAllInMap(map, new DamageCharacter(damagefrom, monsteridfrom, chr.getId(), damage, fake, direction, is_pgmr, pgmr, is_pg, oid, pos_x, pos_y), false, chr);
      } else {
         map.broadcastGMMessage(chr, new DamageCharacter(damagefrom, monsteridfrom, chr.getId(), damage, fake, direction, is_pgmr, pgmr, is_pg, oid, pos_x, pos_y), false);
      }
      if (GameConstants.isDojo(map.getId())) {
         chr.setDojoEnergy(chr.getDojoEnergy() + YamlConfig.config.server.DOJO_ENERGY_DMG);
         PacketCreator.announce(client, new GetEnergy("energy", chr.getDojoEnergy()));
      }

      for (MapleCharacter player : banishPlayers) {  // chill, if this list ever gets non-empty an attacker does exist, trust me :)
         player.changeMapBanish(attacker.getBanish().map(), attacker.getBanish().portal(), attacker.getBanish().msg());
      }
   }
}
