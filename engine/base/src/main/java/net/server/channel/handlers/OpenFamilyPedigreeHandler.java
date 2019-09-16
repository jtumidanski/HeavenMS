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

import client.MapleClient;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.OpenFamilyPedigreePacket;
import net.server.channel.packet.reader.OpenFamilyPedigreeReader;
import tools.MaplePacketCreator;

/**
 * @author Ubaware
 */
public final class OpenFamilyPedigreeHandler extends AbstractPacketHandler<OpenFamilyPedigreePacket> {
   @Override
   public Class<OpenFamilyPedigreeReader> getReaderClass() {
      return OpenFamilyPedigreeReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return ServerConstants.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(OpenFamilyPedigreePacket packet, MapleClient client) {
      client.getChannelServer().getPlayerStorage().getCharacterByName(packet.characterName()).ifPresent(target -> {
         if (target.getFamily() != null) {
            client.announce(MaplePacketCreator.showPedigree(target.getFamilyEntry()));
         }
      });
   }
}

