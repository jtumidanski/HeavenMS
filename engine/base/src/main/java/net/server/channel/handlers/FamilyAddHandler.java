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
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilyAddPacket;
import net.server.channel.packet.reader.FamilyAddReader;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author Jay Estrella
 * @author Ubaware
 */
public final class FamilyAddHandler extends AbstractPacketHandler<FamilyAddPacket> {
   @Override
   public Class<FamilyAddReader> getReaderClass() {
      return FamilyAddReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return ServerConstants.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(FamilyAddPacket packet, MapleClient client) {
      MapleCharacter addChr = client.getChannelServer().getPlayerStorage().getCharacterByName(packet.toAdd()).get();
      MapleCharacter chr = client.getPlayer();
      if (addChr == null) {
         client.announce(MaplePacketCreator.sendFamilyMessage(65, 0));
      } else if (addChr.getMap() != chr.getMap() || (addChr.isHidden()) && chr.gmLevel() < addChr.gmLevel()) {
         client.announce(MaplePacketCreator.sendFamilyMessage(69, 0));
      } else if (addChr.getLevel() <= 10) {
         client.announce(MaplePacketCreator.sendFamilyMessage(77, 0));
      } else if (Math.abs(addChr.getLevel() - chr.getLevel()) > 20) {
         client.announce(MaplePacketCreator.sendFamilyMessage(72, 0));
      } else if (addChr.getFamily() != null && addChr.getFamily() == chr.getFamily()) { //same family
         client.announce(MaplePacketCreator.enableActions());
      } else if (MapleInviteCoordinator.hasInvite(InviteType.FAMILY, addChr.getId())) {
         client.announce(MaplePacketCreator.sendFamilyMessage(73, 0));
      } else if (chr.getFamily() != null && addChr.getFamily() != null && addChr.getFamily().getTotalGenerations() + chr.getFamily().getTotalGenerations() > ServerConstants.FAMILY_MAX_GENERATIONS) {
         client.announce(MaplePacketCreator.sendFamilyMessage(76, 0));
      } else {
         MapleInviteCoordinator.createInvite(InviteType.FAMILY, chr, addChr, addChr.getId());
         addChr.getClient().announce(MaplePacketCreator.sendFamilyInvite(chr.getId(), chr.getName()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "The invite has been sent.");
         client.announce(MaplePacketCreator.enableActions());
      }
   }
}
