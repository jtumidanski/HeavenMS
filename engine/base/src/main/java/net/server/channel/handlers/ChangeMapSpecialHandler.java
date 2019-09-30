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
import net.server.channel.packet.ChangeMapSpecialPacket;
import net.server.channel.packet.reader.ChangeMapSpecialReader;
import server.MapleTrade;
import server.MapleTradeResult;
import server.maps.MaplePortal;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class ChangeMapSpecialHandler extends AbstractPacketHandler<ChangeMapSpecialPacket> {
   @Override
   public Class<ChangeMapSpecialReader> getReaderClass() {
      return ChangeMapSpecialReader.class;
   }

   @Override
   public void handlePacket(ChangeMapSpecialPacket packet, MapleClient client) {
      MaplePortal portal = client.getPlayer().getMap().getPortal(packet.startWarp());
      if (portal == null || client.getPlayer().portalDelay() > currentServerTime() || client.getPlayer().getBlockedPortals().contains(portal.getScriptName())) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      if (client.getPlayer().isChangingMaps() || client.getPlayer().isBanned()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      if (client.getPlayer().getTrade() != null) {
         MapleTrade.cancelTrade(client.getPlayer(), MapleTradeResult.UNSUCCESSFUL_ANOTHER_MAP);
      }
      portal.enterPortal(client);
   }
}
