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
import client.MapleCharacter.FameStatus;
import client.MapleClient;
import client.autoban.AutobanFactory;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GiveFamePacket;
import net.server.channel.packet.reader.GiveFameReader;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

public final class GiveFameHandler extends AbstractPacketHandler<GiveFamePacket, GiveFameReader> {
   @Override
   public Class<GiveFameReader> getReaderClass() {
      return GiveFameReader.class;
   }

   @Override
   public void handlePacket(GiveFamePacket packet, MapleClient client) {
      MapleCharacter target = (MapleCharacter) client.getPlayer().getMap().getMapObject(packet.characterId());
      int mode = packet.mode();
      int fameChange = 2 * mode - 1;
      MapleCharacter player = client.getPlayer();
      if (target == null || target.getId() == player.getId() || player.getLevel() < 15) {
         return;
      } else if (fameChange != 1 && fameChange != -1) {
         AutobanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit fame.");
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to fame hack with fame change " + fameChange);
         client.disconnect(true, false);
         return;
      }

      FameStatus status = player.canGiveFame(target);
      if (status == FameStatus.OK) {
         if (target.gainFame(fameChange, player, mode)) {
            if (!player.isGM()) {
               player.hasGivenFame(target);
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Could not process the request, since this character currently has the minimum/maximum level of fame.");
         }
      } else {
         client.announce(MaplePacketCreator.giveFameErrorResponse(status == FameStatus.NOT_TODAY ? 3 : 4));
      }
   }
}