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
import net.server.AbstractPacketHandler;
import net.server.channel.packet.ReactorHitPacket;
import net.server.channel.packet.reader.ReactorHitReader;
import server.maps.MapleReactor;

/**
 * @author Lerk
 */
public final class ReactorHitHandler extends AbstractPacketHandler<ReactorHitPacket> {
   @Override
   public Class<ReactorHitReader> getReaderClass() {
      return ReactorHitReader.class;
   }

   @Override
   public void handlePacket(ReactorHitPacket packet, MapleClient client) {
      MapleReactor reactor = client.getPlayer().getMap().getReactorByOid(packet.objectId());
      if (reactor != null) {
         reactor.hitReactor(true, packet.characterPosition(), packet.stance(), packet.skillId(), client);
      }
   }
}