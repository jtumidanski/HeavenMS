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
import net.server.PacketReader;
import net.server.channel.packet.movement.MovePlayerPacket;
import net.server.channel.packet.reader.MovePlayerReader;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;

public final class MovePlayerHandler extends AbstractMoveHandler<MovePlayerPacket> {
   @Override
   public Class<? extends PacketReader<MovePlayerPacket>> getReaderClass() {
      return MovePlayerReader.class;
   }

   @Override
   public void handlePacket(MovePlayerPacket packet, MapleClient client) {
      processMovementList(packet.movementDataList(), client.getPlayer());

      if (packet.hasMovement()) {
         client.getPlayer().getMap().movePlayer(client.getPlayer(), client.getPlayer().getPosition());
         if (client.getPlayer().isHidden()) {
            client.getPlayer().getMap().broadcastGMMessage(client.getPlayer(), MaplePacketCreator.movePlayer(client.getPlayer().getId(), packet.movementList()), false);
         } else {
            MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), character -> MaplePacketCreator.movePlayer(client.getPlayer().getId(), packet.movementList()), false, client.getPlayer());
         }
      }
   }
}
