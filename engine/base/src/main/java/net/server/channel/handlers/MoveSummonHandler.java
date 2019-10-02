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

import java.util.Collection;

import client.MapleCharacter;
import client.MapleClient;
import net.server.PacketReader;
import net.server.channel.packet.movement.MoveSummonPacket;
import net.server.channel.packet.reader.MoveSummonReader;
import server.maps.MapleSummon;
import tools.MaplePacketCreator;
import tools.MasterBroadcaster;
import tools.PacketCreator;
import tools.packet.movement.MoveSummon;

public final class MoveSummonHandler extends AbstractMoveHandler<MoveSummonPacket> {
   @Override
   public Class<? extends PacketReader<MoveSummonPacket>> getReaderClass() {
      return MoveSummonReader.class;
   }

   @Override
   public void handlePacket(MoveSummonPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      Collection<MapleSummon> summons = player.getSummonsValues();
      MapleSummon summon = null;
      for (MapleSummon sum : summons) {
         if (sum.getObjectId() == packet.objectId()) {
            summon = sum;
            break;
         }
      }
      if (summon != null) {
         processMovementList(packet.movementDataList(), summon);
         MasterBroadcaster.getInstance().sendToAllInMapRange(player.getMap(),
               character -> PacketCreator.create(new MoveSummon(player.getId(), packet.objectId(), packet.startPosition(), packet.movementList())), player, summon.getPosition());
      }
   }
}
