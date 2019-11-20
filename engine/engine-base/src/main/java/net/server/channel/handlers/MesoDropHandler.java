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
import net.server.channel.packet.MesoDropPacket;
import net.server.channel.packet.reader.MesoDropReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

/**
 * @author Matze
 * @author Ronan - concurrency protection
 */
public final class MesoDropHandler extends AbstractPacketHandler<MesoDropPacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (!player.isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<MesoDropReader> getReaderClass() {
      return MesoDropReader.class;
   }

   @Override
   public void handlePacket(MesoDropPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      if (client.tryAcquireClient()) {     // thanks imbee for noticing players not being able to throw mesos too fast, dampening gameplay of some classes
         try {
            if (packet.meso() <= player.getMeso() && packet.meso() > 9 && packet.meso() < 50001) {
               player.gainMeso(-packet.meso(), false, true, false);
            } else {
               PacketCreator.announce(client, new EnableActions());
               return;
            }
         } finally {
            client.releaseClient();
         }
      } else {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (player.attemptCatchFish(packet.meso())) {
         player.getMap().disappearingMesoDrop(packet.meso(), player, player, player.position());
      } else {
         player.getMap().spawnMesoDrop(packet.meso(), player.position(), player, player, true, (byte) 2);
      }
   }
}