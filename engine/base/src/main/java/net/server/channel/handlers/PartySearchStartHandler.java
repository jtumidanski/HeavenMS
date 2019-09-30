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
import net.server.channel.packet.party.PartySearchStartPacket;
import net.server.channel.packet.reader.PartySearchStartReader;
import net.server.world.MapleParty;
import net.server.world.World;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;

/**
 * @author XoticStory
 * @author BubblesDev
 * @author Ronan
 */
public class PartySearchStartHandler extends AbstractPacketHandler<PartySearchStartPacket> {
   @Override
   public Class<PartySearchStartReader> getReaderClass() {
      return PartySearchStartReader.class;
   }

   @Override
   public void handlePacket(PartySearchStartPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (packet.min() > packet.max()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The min. value is higher than the max!");
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (packet.max() - packet.min() > 30) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You can only search for party members within a range of 30 levels.");
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (chr.getLevel() < packet.min() || chr.getLevel() > packet.max()) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "The range of level for search has to include your own level.");
         PacketCreator.announce(client, new EnableActions());
         return;
      }


      MapleParty party = client.getPlayer().getParty();
      if (party == null || !client.getPlayer().isPartyLeader()) {
         return;
      }

      World world = client.getWorldServer();
      world.getPartySearchCoordinator().registerPartyLeader(chr, packet.min(), packet.max(), packet.jobs());
   }
}