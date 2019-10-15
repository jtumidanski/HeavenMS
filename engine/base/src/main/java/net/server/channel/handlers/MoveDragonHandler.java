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
import net.server.PacketReader;
import net.server.channel.packet.movement.MoveDragonPacket;
import net.server.channel.packet.reader.MoveDragonReader;
import server.maps.MapleDragon;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.movement.MoveDragon;


public class MoveDragonHandler extends AbstractMoveHandler<MoveDragonPacket> {
   @Override
   public Class<? extends PacketReader<MoveDragonPacket>> getReaderClass() {
      return MoveDragonReader.class;
   }

   @Override
   public void handlePacket(MoveDragonPacket packet, MapleClient client) {
      final MapleCharacter chr = client.getPlayer();
      final MapleDragon dragon = chr.getDragon();
      if (dragon != null) {
         processMovementList(packet.movementDataList(), dragon);
         if (packet.hasMovement()) {
            if (chr.isHidden()) {
               chr.getMap().broadcastGMMessage(chr, PacketCreator.create(new MoveDragon(dragon.ownerId(), packet.startPosition(), packet.movementList())));
            } else {
               MasterBroadcaster.getInstance().sendToAllInMapRange(chr.getMap(), new MoveDragon(dragon.ownerId(), packet.startPosition(), packet.movementList()), chr, dragon.position());
            }
         }
      }
   }
}