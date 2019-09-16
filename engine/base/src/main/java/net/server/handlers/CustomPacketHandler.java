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
package net.server.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.CustomPacket;
import net.server.packet.reader.CustomReader;
import tools.MaplePacketCreator;

public class CustomPacketHandler extends AbstractPacketHandler<CustomPacket> {
   @Override
   public Class<CustomReader> getReaderClass() {
      return CustomReader.class;
   }

   @Override
   public void handlePacket(CustomPacket packet, MapleClient client) {
      if (packet.bytes().length > 0 && client.getGMLevel() == 4) {
         client.announce(MaplePacketCreator.customPacket(packet.bytes()));
      }
   }

   @Override
   public boolean validateState(MapleClient c) {
      return true;
   }
}
