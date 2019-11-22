/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2018 RonanLana

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

import java.util.Collections;
import java.util.List;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.processor.maps.MapleMapObjectProcessor;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.buff.GiveBuff;
import tools.packet.spawn.StopMonsterControl;

/**
 * @author Ronan
 */
public final class PlayerMapTransitionHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.setMapTransitionComplete();

      int beaconid = chr.getBuffSource(MapleBuffStat.HOMING_BEACON);
      if (beaconid != -1) {
         chr.cancelBuffStats(MapleBuffStat.HOMING_BEACON);

         final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.HOMING_BEACON, 0));
         PacketCreator.announce(chr, new GiveBuff(1, beaconid, stat));
      }

      if (!chr.isHidden()) {  // thanks Lame for noticing hidden characters controlling mobs
         for (MapleMapObject mo : chr.getMap().getMonsters()) {    // thanks BHB, IxianMace, Jefe for noticing several issues regarding mob statuses (such as freeze)
            MapleMonster m = (MapleMonster) mo;
            if (m.getSpawnEffect() == 0 || m.getHp() < m.getMaxHp()) {     // avoid effect-spawning mobs
               if (m.getController() == chr) {
                  PacketCreator.announce(client, new StopMonsterControl(m.objectId()));
                  MapleMapObjectProcessor.getInstance().sendDestroyData(m, client);
                  m.aggroRedirectController();
               } else {
                  MapleMapObjectProcessor.getInstance().sendDestroyData(m, client);
               }

               m.aggroSwitchController(chr, false);
               MapleMapObjectProcessor.getInstance().sendDestroyData(m, client);
            }
         }
      }
   }
}