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

import client.MapleClient;
import client.autoban.AutobanFactory;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetChatPacket;
import net.server.channel.packet.reader.PetChatReader;
import tools.FilePrinter;
import tools.LogHelper;
import tools.MasterBroadcaster;
import tools.packet.pet.PetChat;

public final class PetChatHandler extends AbstractPacketHandler<PetChatPacket> {
   @Override
   public Class<PetChatReader> getReaderClass() {
      return PetChatReader.class;
   }

   @Override
   public void handlePacket(PetChatPacket packet, MapleClient client) {
      byte pet = client.getPlayer().getPetIndex(packet.petId());
      if ((pet < 0 || pet > 3) || (packet.act() < 0 || packet.act() > 9)) {
         return;
      }
      if (packet.text().length() > Byte.MAX_VALUE) {
         AutobanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit with pets.");
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to send text with length of " + packet.text().length());
         client.disconnect(true, false);
         return;
      }
      MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new PetChat(client.getPlayer().getId(), pet, packet.act(), packet.text()), true, client.getPlayer());
      if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Pet", packet.text());
      }
   }
}
