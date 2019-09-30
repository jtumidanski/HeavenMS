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

import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.ServerConstants;
import constants.skills.Brawler;
import constants.skills.Corsair;
import constants.skills.Priest;
import constants.skills.SuperGM;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.Server;
import net.server.channel.packet.reader.SpecialMoveReader;
import net.server.channel.packet.special.BaseSpecialMovePacket;
import net.server.channel.packet.special.MonsterMagnetPacket;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.processor.StatEffectProcessor;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

public final class SpecialMoveHandler extends AbstractPacketHandler<BaseSpecialMovePacket> {
   @Override
   public Class<? extends PacketReader<BaseSpecialMovePacket>> getReaderClass() {
      return SpecialMoveReader.class;
   }

   @Override
   public void handlePacket(BaseSpecialMovePacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.getAutobanManager().setTimestamp(4, Server.getInstance().getCurrentTimestamp(), 28);

      Skill skill = SkillFactory.getSkill(packet.skillId()).orElseThrow();
      int skillLevel = chr.getSkillLevel(skill);
      if (packet.skillId() % 10000000 == 1010 || packet.skillId() % 10000000 == 1011) {
         if (chr.getDojoEnergy() < 10000) { // PE hacking or maybe just lagging
            return;
         }
         skillLevel = 1;
         chr.setDojoEnergy(0);
         client.announce(MaplePacketCreator.getEnergy("energy", chr.getDojoEnergy()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "As you used the secret skill, your energy bar has been reset.");
      }
      if (skillLevel == 0 || skillLevel != packet.skillLevel()) {
         return;
      }

      MapleStatEffect effect = skill.getEffect(skillLevel);
      if (effect.getCooldown() > 0) {
         if (chr.skillIsCooling(packet.skillId())) {
            return;
         } else if (packet.skillId() != Corsair.BATTLE_SHIP) {
            int cooldownTime = effect.getCooldown();
            if (StatEffectProcessor.getInstance().isHerosWill(packet.skillId()) && ServerConstants.USE_FAST_REUSE_HERO_WILL) {
               cooldownTime /= 60;
            }

            client.announce(MaplePacketCreator.skillCooldown(packet.skillId(), cooldownTime));
            chr.addCooldown(packet.skillId(), currentServerTime(), cooldownTime * 1000);
         }
      }
      if (packet instanceof MonsterMagnetPacket) { // Monster Magnet
         int num = ((MonsterMagnetPacket) packet).monsterData().length;
         for (int i = 0; i < num; i++) {
            int mobOid = ((MonsterMagnetPacket) packet).monsterData()[i].monsterId();
            byte success = ((MonsterMagnetPacket) packet).monsterData()[i].success();
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), character ->MaplePacketCreator.catchMonster(mobOid, success), false, chr);
            MapleMonster monster = chr.getMap().getMonsterByOid(mobOid);
            if (monster != null) {
               if (!monster.isBoss()) {
                  monster.aggroClearDamages();
                  monster.aggroMonsterDamage(chr, 1);

                  // thanks onechord for pointing out Magnet crashing the caster (issue would actually happen upon failing to catch mob)
                  // thanks Conrad for noticing Magnet crashing when trying to pull bosses and fixed mobs
                  monster.aggroSwitchController(chr, true);
               }
            }
         }
         byte direction = ((MonsterMagnetPacket) packet).direction();   // thanks MedicOP for pointing some 3rd-party related issues with Magnet
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), character ->MaplePacketCreator.showBuffeffect(chr.getId(), packet.skillId(), chr.getSkillLevel(packet.skillId()), 1, direction), false, chr);
         PacketCreator.announce(client, new EnableActions());
         return;
      } else if (packet.skillId() == Brawler.MP_RECOVERY) {// MP Recovery
         SkillFactory.getSkill(packet.skillId()).ifPresent(s -> {
            MapleStatEffect ef = s.getEffect(chr.getSkillLevel(s));
            int lose = chr.safeAddHP(-1 * (chr.getCurrentMaxHp() / ef.getX()));
            int gain = -lose * (ef.getY() / 100);
            chr.addMP(gain);
         });
      } else if (packet.skillId() == SuperGM.HEAL_PLUS_DISPEL) {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), character ->MaplePacketCreator.showBuffeffect(chr.getId(), packet.skillId(), chr.getSkillLevel(packet.skillId())), false, chr);
      }

      Point pos = packet.position();
      if (chr.isAlive()) {
         if (skill.getId() != Priest.MYSTIC_DOOR) {
            if (skill.getId() % 10000000 != 1005) {
               skill.getEffect(skillLevel).applyTo(chr, pos);
            } else {
               skill.getEffect(skillLevel).applyEchoOfHero(chr);
            }
         } else {
            if (client.tryAcquireClient()) {
               try {
                  if (chr.canDoor()) {
                     chr.cancelMagicDoor();
                     skill.getEffect(skillLevel).applyTo(chr, pos);
                  } else {
                     MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Please wait 5 seconds before casting Mystic Door again.");
                  }
               } finally {
                  client.releaseClient();
               }
            }

            PacketCreator.announce(client, new EnableActions());
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
      }
   }
}