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

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.FamilyAddPacket;
import net.server.channel.packet.reader.FamilyAddReader;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.family.FamilyMessage;
import tools.packet.family.SendFamilyInvite;
import tools.packet.stat.EnableActions;

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
      Optional<MapleCharacter> addChr = client.getChannelServer().getPlayerStorage().getCharacterByName(packet.toAdd());
      MapleCharacter chr = client.getPlayer();
      if (addChr.isEmpty()) {
         PacketCreator.announce(client, new FamilyMessage(65, 0));
      } else if (addChr.get() == chr) { //only possible through packet editing/client editing i think?
         PacketCreator.announce(client, new EnableActions());
      } else if (addChr.get().getMap() != chr.getMap() || (addChr.get().isHidden()) && chr.gmLevel() < addChr.get().gmLevel()) {
         PacketCreator.announce(client, new FamilyMessage(69, 0));
      } else if (addChr.get().getLevel() <= 10) {
         PacketCreator.announce(client, new FamilyMessage(77, 0));
      } else if (Math.abs(addChr.get().getLevel() - chr.getLevel()) > 20) {
         PacketCreator.announce(client, new FamilyMessage(72, 0));
      } else if (addChr.get().getFamily() != null && addChr.get().getFamily() == chr.getFamily()) { //same family
         PacketCreator.announce(client, new EnableActions());
      } else if (MapleInviteCoordinator.hasInvite(InviteType.FAMILY, addChr.get().getId())) {
         PacketCreator.announce(client, new FamilyMessage(73, 0));
      } else if (chr.getFamily() != null && addChr.get().getFamily() != null && addChr.get().getFamily().getTotalGenerations() + chr.getFamily().getTotalGenerations() > ServerConstants.FAMILY_MAX_GENERATIONS) {
         PacketCreator.announce(client, new FamilyMessage(76, 0));
      } else {
         MapleInviteCoordinator.createInvite(InviteType.FAMILY, chr, addChr.get(), addChr.get().getId());
         PacketCreator.announce(addChr.get(), new SendFamilyInvite(chr.getId(), chr.getName()));
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "The invite has been sent.");
         PacketCreator.announce(client, new EnableActions());
      }
   }
}
