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
import client.command.CommandsExecutor;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GeneralChatPacket;
import net.server.channel.packet.reader.GeneralChatReader;
import tools.FilePrinter;
import tools.LogHelper;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.message.ChatText;
import tools.packet.stat.EnableActions;

public final class GeneralChatHandler extends AbstractPacketHandler<GeneralChatPacket> {
   @Override
   public Class<GeneralChatReader> getReaderClass() {
      return GeneralChatReader.class;
   }

   @Override
   public void handlePacket(GeneralChatPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.getAutobanManager().getLastSpam(7) + 200 > currentServerTime()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      if (packet.message().length() > Byte.MAX_VALUE && !chr.isGM()) {
         AutobanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit in General Chat.");
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to send text with length of " + packet.message().length());
         client.disconnect(true, false);
         return;
      }
      char heading = packet.message().charAt(0);
      if (CommandsExecutor.isCommand(client, packet.message())) {
         CommandsExecutor.getInstance().handle(client, packet.message());
      } else if (heading != '/') {
         if (chr.getMap().isMuted() && !chr.isGM()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "The map you are in is currently muted. Please try again later.");
            return;
         }

         if (!chr.isHidden()) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), character -> PacketCreator.create(new ChatText(chr.getId(), packet.message(), chr.getWhiteChat(), packet.show())));
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "General", packet.message());
            }
         } else {
            MasterBroadcaster.getInstance().sendToAllGMInMap(chr.getMap(), character -> PacketCreator.create(new ChatText(chr.getId(), packet.message(), chr.getWhiteChat(), packet.show())));
            if (ServerConstants.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "GM General", packet.message());
            }
         }

         chr.getAutobanManager().spam(7);
      }
   }
}