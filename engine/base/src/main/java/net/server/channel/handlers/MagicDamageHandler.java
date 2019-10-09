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

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.ServerConstants;
import constants.skills.Bishop;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.ILArchMage;
import net.server.channel.packet.AttackPacket;
import net.server.channel.packet.reader.DamageReader;
import net.server.channel.worker.PacketReaderFactory;
import server.MapleStatEffect;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.GetEnergy;
import tools.packet.attack.CloseRangeAttack;
import tools.packet.attack.MagicAttack;
import tools.packet.character.SkillCooldown;

public final class MagicDamageHandler extends AbstractDealDamageHandler<AttackPacket> {
   @Override
   public Class<DamageReader> getReaderClass() {
      return DamageReader.class;
   }

   @Override
   public void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      DamageReader damageReader = (DamageReader) PacketReaderFactory.getInstance().get(getReaderClass());
      handlePacket(damageReader.read(accessor, client.getPlayer(), false, false), client);
   }

   @Override
   public void handlePacket(AttackPacket attack, MapleClient c) {
      MapleCharacter chr = c.getPlayer();
      if (chr.getBuffEffect(MapleBuffStat.MORPH) != null) {
         if (chr.getBuffEffect(MapleBuffStat.MORPH).isMorphWithoutAttack()) {
            // How are they attacking when the client won't let them?
            chr.getClient().disconnect(false, false);
            return;
         }
      }

      if (chr.getMap().isDojoMap() && attack.numAttacked() > 0) {
         chr.setDojoEnergy(chr.getDojoEnergy() + +ServerConstants.DOJO_ENERGY_ATK);
         PacketCreator.announce(c, new GetEnergy("energy", chr.getDojoEnergy()));
      }

      byte[] packet;
      if ((attack.skill() == Evan.FIRE_BREATH || attack.skill() == Evan.ICE_BREATH || attack.skill() == FPArchMage.BIG_BANG || attack.skill() == ILArchMage.BIG_BANG || attack.skill() == Bishop.BIG_BANG)) {
         packet = PacketCreator.create(new MagicAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), attack.getDamage(), attack.charge(), attack.speed(), attack.direction(), attack.display()));
      } else {
         packet = PacketCreator.create(new CloseRangeAttack(chr.getId(), attack.skill(), attack.skillLevel(), attack.stance(), attack.numAttackedAndDamage(), attack.getDamage(), attack.speed(), attack.direction(), attack.display()));
      }

      MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), character -> packet, false, chr, true);
      MapleStatEffect effect = getAttackEffect(attack, chr, null);

      SkillFactory.getSkill(attack.skill()).ifPresent(skill -> {
         MapleStatEffect effect_ = skill.getEffect(chr.getSkillLevel(skill));
         if (effect_.getCooldown() > 0) {
            if (!chr.skillIsCooling(attack.skill())) {
               PacketCreator.announce(c, new SkillCooldown(attack.skill(), effect_.getCooldown()));
               chr.addCooldown(attack.skill(), currentServerTime(), effect_.getCooldown() * 1000);
            }
         }
      });

      applyAttack(attack, chr, effect.getAttackCount());

      // MP Eater, works with right job
      int mpEaterSkillId = (chr.getJob().getId() - (chr.getJob().getId() % 10)) * 10000;
      SkillFactory.executeIfHasSkill(chr, mpEaterSkillId, (skill, skillLevel) -> {
         for (Integer singleDamage : attack.getDamage().keySet()) {
            skill.getEffect(skillLevel).applyPassive(chr, chr.getMap().getMapObject(singleDamage), 0);
         }
      });
   }
}
