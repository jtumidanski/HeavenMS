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

package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.SetGenderReader;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.login.packet.SetGenderPacket;
import tools.MaplePacketCreator;

/**
 * @author kevintjuh93
 */
public class SetGenderHandler extends AbstractPacketHandler<SetGenderPacket, SetGenderReader> {
   @Override
   public Class<SetGenderReader> getReaderClass() {
      return SetGenderReader.class;
   }

   @Override
   public void handlePacket(SetGenderPacket packet, MapleClient client) {
      if (packet.confirmed() == 0x01) {
         client.setGender(packet.gender());
         client.announce(MaplePacketCreator.getAuthSuccess(client));

         Server.getInstance().registerLoginState(client);
      } else {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
         client.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
      }
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return client.getGender() == 10;
   }
}
