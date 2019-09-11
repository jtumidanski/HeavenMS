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

import client.MapleCharacter;
import client.MapleClient;
import constants.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.FieldDamageMobPacket;
import net.server.channel.packet.reader.FieldDamageMobReader;
import server.life.MapleMonster;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleMap;
import tools.FilePrinter;
import tools.MaplePacketCreator;

public class FieldDamageMobHandler extends AbstractPacketHandler<FieldDamageMobPacket, FieldDamageMobReader> {
   @Override
   public Class<FieldDamageMobReader> getReaderClass() {
      return FieldDamageMobReader.class;
   }

   @Override
   public void handlePacket(FieldDamageMobPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      MapleMap map = chr.getMap();

      if (map.getEnvironment().isEmpty()) {   // no environment objects activated to actually hit the mob
         FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to use an obstacle on mapid " + map.getId() + " to attack.");
         return;
      }

      MapleMonster mob = map.getMonsterByOid(packet.mobId());
      if (mob != null) {

         if (packet.damage() < 0 || packet.damage() > GameConstants.MAX_FIELD_MOB_DAMAGE) {
            map.broadcastMessage(chr, MaplePacketCreator.damageMonster(packet.mobId(), packet.damage()), true);
            FilePrinter.printError(FilePrinter.EXPLOITS + client.getPlayer().getName() + ".txt", client.getPlayer().getName() + " tried to use an obstacle on mapid " + map.getId() + " to attack " + MapleMonsterInformationProvider.getInstance().getMobNameFromId(mob.getId()) + " with damage " + packet.damage());
            return;
         }
      }
      map.damageMonster(chr, mob, packet.damage());
   }
}
