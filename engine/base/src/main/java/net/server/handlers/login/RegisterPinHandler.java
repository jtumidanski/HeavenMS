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
import net.server.channel.packet.reader.RegisterPinReader;
import net.server.coordinator.MapleSessionCoordinator;
import net.server.login.packet.RegisterPinPacket;
import tools.PacketCreator;
import tools.packet.pin.PinRegistered;

/*
 * @author Rob
 */
public final class RegisterPinHandler extends AbstractPacketHandler<RegisterPinPacket> {
   @Override
   public Class<RegisterPinReader> getReaderClass() {
      return RegisterPinReader.class;
   }

   @Override
   public void handlePacket(RegisterPinPacket packet, MapleClient client) {
      if (packet.byte1() == 0) {
         MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
         client.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
      } else {
         if (packet.pin() != null) {
            client.setPin(packet.pin());
            PacketCreator.announce(client, new PinRegistered());

            MapleSessionCoordinator.getInstance().closeSession(client.getSession(), null);
            client.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN);
         }
      }
   }
}
