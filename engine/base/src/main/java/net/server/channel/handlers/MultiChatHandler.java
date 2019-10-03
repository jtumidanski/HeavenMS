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
import client.autoban.AutobanFactory;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.MultiChatPacket;
import net.server.channel.packet.reader.MultiChatReader;
import net.server.world.World;
import tools.FilePrinter;
import tools.LogHelper;
import tools.PacketCreator;
import tools.packet.message.MultiChat;

public final class MultiChatHandler extends AbstractPacketHandler<MultiChatPacket> {
   @Override
   public Class<MultiChatReader> getReaderClass() {
      return MultiChatReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter player = client.getPlayer();
      return player.getAutobanManager().getLastSpam(7) + 200 <= currentServerTime();
   }

   @Override
   public void handlePacket(MultiChatPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();

      if (packet.message().length() > Byte.MAX_VALUE && !player.isGM()) {
         AutobanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit chats.");
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to send text with length of " + packet.message().length());
         client.disconnect(true, false);
         return;
      }
      World world = client.getWorldServer();
      if (packet.theType() == 0) {
         buddyChat(packet, client, player, world);
      } else if (packet.theType() == 1 && player.getParty() != null) {
         partyChat(packet, client, player, world);
      } else if (packet.theType() == 2 && player.getGuildId() > 0) {
         guildChat(packet, client, player);
      } else if (packet.theType() == 3) {
         allianceChat(packet, client, player);
      }
      player.getAutobanManager().spam(7);
   }

   private void allianceChat(MultiChatPacket packet, MapleClient client, MapleCharacter player) {
      player.getGuild().ifPresent(guild -> {
         int allianceId = guild.getAllianceId();
         if (allianceId > 0) {
            Server.getInstance().allianceMessage(allianceId, PacketCreator.create(new MultiChat(player.getName(), packet.message(), 3)), player.getId(), -1);
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Ally", packet.message());
            }
         }
      });
   }

   private void guildChat(MultiChatPacket packet, MapleClient client, MapleCharacter player) {
      Server.getInstance().guildChat(player.getGuildId(), player.getName(), player.getId(), packet.message());
      if (ServerConstants.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Guild", packet.message());
      }
   }

   private void partyChat(MultiChatPacket packet, MapleClient client, MapleCharacter player, World world) {
      world.partyChat(player.getParty(), packet.message(), player.getName());
      if (ServerConstants.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Party", packet.message());
      }
   }

   private void buddyChat(MultiChatPacket packet, MapleClient client, MapleCharacter player, World world) {
      world.buddyChat(packet.recipientIds(), player.getId(), player.getName(), packet.message());
      if (ServerConstants.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Buddy", packet.message());
      }
   }
}
