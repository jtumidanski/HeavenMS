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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleJob;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import constants.skills.Beginner;
import constants.skills.Crusader;
import constants.skills.DawnWarrior;
import constants.skills.DragonKnight;
import constants.skills.Hero;
import constants.skills.Legend;
import constants.skills.Marauder;
import constants.skills.NightWalker;
import constants.skills.Noblesse;
import constants.skills.Paladin;
import constants.skills.Rogue;
import constants.skills.ThunderBreaker;
import constants.skills.WhiteKnight;
import constants.skills.WindArcher;
import server.MapleStatEffect;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.Pair;
import tools.ServerNoticeType;
import tools.data.input.SeekableLittleEndianAccessor;

public final class CloseRangeDamageHandler extends AbstractDealDamageHandler {
   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      //chr.setPetLootCd(currentServerTime());
        
        /*long timeElapsed = currentServerTime() - chr.getAutobanManager().getLastSpam(8);
        if(timeElapsed < 300) {
                AutobanFactory.FAST_ATTACK.alert(chr, "Time: " + timeElapsed);
        }
        chr.getAutobanManager().spam(8);*/

      AttackInfo attack = parseDamage(slea, chr, false, false);
      if (chr.getBuffEffect(MapleBuffStat.MORPH) != null) {
         if (chr.getBuffEffect(MapleBuffStat.MORPH).isMorphWithoutAttack()) {
            // How are they attacking when the client won't let them?
            chr.getClient().disconnect(false, false);
            return;
         }
      }

      if (chr.getDojoEnergy() < 10000 && (attack.skill == Beginner.BAMBOO_RAIN || attack.skill == Noblesse.BAMBOO_RAIN || attack.skill == Legend.BAMBOO_THRUST)) {
         // PE hacking or maybe just lagging
         return;
      }
      if (chr.getMap().isDojoMap() && attack.numAttacked > 0) {
         chr.setDojoEnergy(chr.getDojoEnergy() + ServerConstants.DOJO_ENERGY_ATK);
         c.announce(MaplePacketCreator.getEnergy("energy", chr.getDojoEnergy()));
      }

      chr.getMap().broadcastMessage(chr, MaplePacketCreator.closeRangeAttack(chr, attack.skill, attack.skilllevel, attack.stance, attack.numAttackedAndDamage, attack.allDamage, attack.speed, attack.direction, attack.display), false, true);
      int numFinisherOrbs = 0;
      Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);
      if (GameConstants.isFinisherSkill(attack.skill)) {
         if (comboBuff != null) {
            numFinisherOrbs = comboBuff - 1;
         }
         chr.handleOrbconsume();
      } else if (attack.numAttacked > 0) {
         if (attack.skill != Crusader.SHOUT && comboBuff != null) {
            int orbCount = chr.getBuffedValue(MapleBuffStat.COMBO);
            int comboId = chr.isCygnus() ? DawnWarrior.COMBO : Crusader.COMBO;
            int advancedComboId = chr.isCygnus() ? DawnWarrior.ADVANCED_COMBO : Hero.ADVANCED_COMBO;

            Optional<MapleStatEffect> comboEffect = Optional.ofNullable(SkillFactory.applyIfHasSkill(chr, advancedComboId, Skill::getEffect, null));
            if (comboEffect.isEmpty()) {
               int comboLv = SkillFactory.getSkill(comboId).map(chr::getSkillLevel).orElse((byte) 0);
               if (comboLv <= 0 || chr.isGM()) {
                  comboLv = SkillFactory.getSkill(comboId).map(Skill::getMaxLevel).orElse(0);
               }

               if (comboLv > 0) {
                  int finalComboLv = comboLv;
                  comboEffect = SkillFactory.getSkill(comboId).map(skill -> skill.getEffect(finalComboLv));
               } else {
                  comboEffect = Optional.empty();
               }
            }

            if (comboEffect.isPresent()) {
               if (orbCount < comboEffect.get().getX() + 1) {
                  int newOrbCount = orbCount + 1;
                  int advComboSkillLevel = SkillFactory.getSkill(advancedComboId).map(chr::getSkillLevel).orElse((byte) 0);
                  if (advComboSkillLevel > 0 && comboEffect.get().makeChanceResult()) {
                     if (newOrbCount <= comboEffect.get().getX()) {
                        newOrbCount++;
                     }
                  }

                  final int comboLevel = chr.getSkillLevel(comboId) > 0
                        ? chr.getSkillLevel(comboId)
                        : SkillFactory.getSkill(comboId).map(Skill::getMaxLevel).orElse(0);
                  int duration = SkillFactory.getSkill(comboId).map(skill -> skill.getEffect(comboLevel)).map(MapleStatEffect::getDuration).orElse(0);

                  List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, newOrbCount));
                  chr.setBuffedValue(MapleBuffStat.COMBO, newOrbCount);
                  duration -= (int) (currentServerTime() - chr.getBuffedStarttime(MapleBuffStat.COMBO));
                  c.announce(MaplePacketCreator.giveBuff(comboId, duration, stat));
                  chr.getMap().broadcastMessage(chr, MaplePacketCreator.giveForeignBuff(chr.getId(), stat), false);
               }
            }
         } else if (chr.getJob().isA(MapleJob.MARAUDER)) {
            SkillFactory.executeIfHasSkill(chr, Marauder.ENERGY_CHARGE, (skill, skillLevel) -> chargeNEnergy(chr, attack.numAttacked));
         } else if (chr.getJob().isA(MapleJob.THUNDERBREAKER2)) {
            SkillFactory.executeIfHasSkill(chr, ThunderBreaker.ENERGY_CHARGE, (skill, skillLevel) -> chargeNEnergy(chr, attack.numAttacked));
         }
      }
      if (attack.numAttacked > 0 && attack.skill == DragonKnight.SACRIFICE) {
         int totDamageToOneMonster = 0; // sacrifice attacks only 1 mob with 1 attack
         final Iterator<List<Integer>> dmgIt = attack.allDamage.values().iterator();
         if (dmgIt.hasNext()) {
            totDamageToOneMonster = dmgIt.next().get(0);
         }

         chr.safeAddHP(-1 * totDamageToOneMonster * attack.getAttackEffect(chr, null).getX() / 100);
      }
      if (attack.numAttacked > 0 && attack.skill == WhiteKnight.CHARGE_BLOW) {
         SkillFactory.executeIfHasSkill(chr, Paladin.ADVANCED_CHARGE, (skill, skillLevel) -> {
            boolean advanceChargeProbability = false;
            if (skillLevel > 0) {
               advanceChargeProbability = skill.getEffect(skillLevel).makeChanceResult();
            }
            if (!advanceChargeProbability) {
               chr.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
            }
         });
      }
      int attackCount = 1;
      if (attack.skill != 0) {
         attackCount = attack.getAttackEffect(chr, null).getAttackCount();
      }
      if (numFinisherOrbs == 0 && GameConstants.isFinisherSkill(attack.skill)) {
         return;
      }
      if (attack.skill % 10000000 == 1009) { // bamboo
         if (chr.getDojoEnergy() < 10000) { // PE hacking or maybe just lagging
            return;
         }

         chr.setDojoEnergy(0);
         c.announce(MaplePacketCreator.getEnergy("energy", chr.getDojoEnergy()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "As you used the secret skill, your energy bar has been reset.");
      } else if (attack.skill > 0) {
         SkillFactory.executeForSkill(chr, attack.skill, ((skill, skillLevel) -> {
            MapleStatEffect effect_ = skill.getEffect(chr.getSkillLevel(skill));
            if (effect_.getCooldown() > 0) {
               if (!chr.skillIsCooling(attack.skill)) {
                  c.announce(MaplePacketCreator.skillCooldown(attack.skill, effect_.getCooldown()));
                  chr.addCooldown(attack.skill, currentServerTime(), effect_.getCooldown() * 1000);
               }
            }
         }));
      }
      if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null) {
         SkillFactory.executeIfHasSkill(chr, NightWalker.VANISH, (skill, skillLevel) -> cancelDarkSight(chr));
         SkillFactory.executeIfHasSkill(chr, Rogue.DARK_SIGHT, (skill, skillLevel) -> cancelDarkSight(chr));
      } else if (chr.getBuffedValue(MapleBuffStat.WIND_WALK) != null) {
         SkillFactory.executeIfHasSkill(chr, WindArcher.WIND_WALK, (skill, skillLevel) -> cancelWindWalk(chr));
      }
      applyAttack(attack, chr, attackCount);
   }

   private void chargeNEnergy(MapleCharacter character, int numberOfAttacks) {
      IntStream.generate(() -> 1).limit(numberOfAttacks).forEach(id -> character.handleEnergyChargeGain());
   }

   private void cancelWindWalk(MapleCharacter chr) {
      chr.cancelEffectFromBuffStat(MapleBuffStat.WIND_WALK);
      chr.cancelBuffStats(MapleBuffStat.WIND_WALK);
   }

   private void cancelDarkSight(MapleCharacter chr) {
      chr.cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
      chr.cancelBuffStats(MapleBuffStat.DARKSIGHT);
   }
}