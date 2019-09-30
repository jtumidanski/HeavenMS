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
import net.server.channel.packet.DoorPacket;
import net.server.channel.packet.reader.DoorReader;
import server.maps.MapleDoorObject;
import server.maps.MapleMapObject;
import tools.MaplePacketCreator;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

/**
 * @author Matze
 */
public final class DoorHandler extends AbstractPacketHandler<DoorPacket> {
   @Override
   public Class<DoorReader> getReaderClass() {
      return DoorReader.class;
   }

   @Override
   public void handlePacket(DoorPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.isChangingMaps() || chr.isBanned()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      for (MapleMapObject obj : chr.getMap().getMapObjects()) {
         if (obj instanceof MapleDoorObject) {
            MapleDoorObject door = (MapleDoorObject) obj;
            if (door.getOwnerId() == packet.ownerId()) {
               door.warp(chr);
               return;
            }
         }
      }

      client.announce(MaplePacketCreator.blockedMessage(6));
      PacketCreator.announce(client, new EnableActions());
   }
}
