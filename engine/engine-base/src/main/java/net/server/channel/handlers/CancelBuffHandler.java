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

import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import constants.skills.Bishop;
import constants.skills.Bowmaster;
import constants.skills.Corsair;
import constants.skills.Evan;
import constants.skills.FPArchMage;
import constants.skills.ILArchMage;
import constants.skills.Marksman;
import constants.skills.WindArcher;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CancelBuffPacket;
import net.server.channel.packet.reader.CancelBuffReader;
import tools.MasterBroadcaster;
import tools.packet.foreigneffect.CancelSkill;

public final class CancelBuffHandler extends AbstractPacketHandler<CancelBuffPacket> {
   @Override
   public Class<CancelBuffReader> getReaderClass() {
      return CancelBuffReader.class;
   }

   @Override
   public void handlePacket(CancelBuffPacket packet, MapleClient client) {
      MapleCharacter character = client.getPlayer();
      switch (packet.sourceId()) {
         case FPArchMage.BIG_BANG:
         case ILArchMage.BIG_BANG:
         case Bishop.BIG_BANG:
         case Bowmaster.HURRICANE:
         case Marksman.PIERCING_ARROW:
         case Corsair.RAPID_FIRE:
         case WindArcher.HURRICANE:
         case Evan.FIRE_BREATH:
         case Evan.ICE_BREATH:
            MasterBroadcaster.getInstance().sendToAllInMap(character.getMap(), new CancelSkill(character.getId(), packet.sourceId()), false, character);
            break;
         default:
            SkillFactory.getSkill(packet.sourceId()).ifPresent(skill -> character.cancelEffect(skill.getEffect(1), false, -1));
            break;
      }
   }
}