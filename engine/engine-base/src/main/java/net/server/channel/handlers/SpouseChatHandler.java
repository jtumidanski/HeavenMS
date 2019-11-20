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
import net.server.channel.packet.SpouseChatPacket;
import net.server.channel.packet.reader.SpouseChatReader;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.message.SpouseMessage;

public final class SpouseChatHandler extends AbstractPacketHandler<SpouseChatPacket> {
   @Override
   public Class<SpouseChatReader> getReaderClass() {
      return SpouseChatReader.class;
   }

   @Override
   public void handlePacket(SpouseChatPacket packet, MapleClient client) {

      int partnerId = client.getPlayer().getPartnerId();
      if (partnerId > 0) { // yay marriage
         Optional<MapleCharacter> spouse = client.getWorldServer().getPlayerStorage().getCharacterById(partnerId);
         if (spouse.isPresent()) {
            PacketCreator.announce(spouse.get(), new SpouseMessage(client.getPlayer().getName(), packet.message(), true));
            PacketCreator.announce(client, new SpouseMessage(client.getPlayer().getName(), packet.message(), true));
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Spouse", packet.message());
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "Your spouse is currently offline.");
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, "You don't have a spouse.");
      }
   }
}
