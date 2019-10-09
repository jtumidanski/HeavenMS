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
import net.server.channel.packet.AddTeleportRockMapPacket;
import net.server.channel.packet.BaseTeleportRockMapPacket;
import net.server.channel.packet.DeleteTeleportRockMapPacket;
import net.server.channel.packet.reader.TeleportRockMapReader;
import server.maps.FieldLimit;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.ui.RefreshTeleportRockMapList;

/**
 * @author kevintjuh93
 */
public final class TrockAddMapHandler extends AbstractPacketHandler<BaseTeleportRockMapPacket> {
   @Override
   public Class<TeleportRockMapReader> getReaderClass() {
      return TeleportRockMapReader.class;
   }

   @Override
   public void handlePacket(BaseTeleportRockMapPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (packet instanceof DeleteTeleportRockMapPacket) {
         if (packet.vip()) {
            chr.deleteFromVipTrocks(((DeleteTeleportRockMapPacket) packet).mapId());
         } else {
            chr.deleteFromTrocks(((DeleteTeleportRockMapPacket) packet).mapId());
         }
         PacketCreator.announce(client, new RefreshTeleportRockMapList(chr.getVipTrockMaps(), chr.getTrockMaps(), true, packet.vip()));
      } else if (packet instanceof AddTeleportRockMapPacket) {
         if (!FieldLimit.CANNOTVIPROCK.check(chr.getMap().getFieldLimit())) {
            if (packet.vip()) {
               chr.addVipTrockMap();
            } else {
               chr.addTrockMap();
            }

            PacketCreator.announce(client, new RefreshTeleportRockMapList(chr.getVipTrockMaps(), chr.getTrockMaps(), false, packet.vip()));
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "You may not save this map.");
         }
      }
   }
}
