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
import net.server.AbstractPacketHandler;
import net.server.channel.packet.party.DenyPartyRequestPacket;
import net.server.channel.packet.reader.DenyPartyRequestReader;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import tools.MaplePacketCreator;

public final class DenyPartyRequestHandler extends AbstractPacketHandler<DenyPartyRequestPacket> {
   @Override
   public Class<DenyPartyRequestReader> getReaderClass() {
      return DenyPartyRequestReader.class;
   }

   @Override
   public void handlePacket(DenyPartyRequestPacket packet, MapleClient client) {
      String[] cname = packet.message().split("PS: ");
      client.getChannelServer().getPlayerStorage().getCharacterByName(cname[cname.length - 1]).ifPresent(characterFrom -> {
         MapleCharacter chr = client.getPlayer();
         if (MapleInviteCoordinator.answerInvite(InviteType.PARTY, chr.getId(), characterFrom.getPartyId(), false).result == InviteResult.DENIED) {
            chr.updatePartySearchAvailability(chr.getParty() == null);
            characterFrom.getClient().announce(MaplePacketCreator.partyStatusMessage(23, chr.getName()));
         }
      });
   }
}
