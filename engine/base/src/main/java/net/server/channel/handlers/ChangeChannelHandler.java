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
import client.autoban.AutobanFactory;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.ChangeChannelPacket;
import net.server.channel.packet.reader.ChangeChannelReader;

/**
 * @author Matze
 */
public final class ChangeChannelHandler extends AbstractPacketHandler<ChangeChannelPacket> {
   @Override
   public Class<ChangeChannelReader> getReaderClass() {
      return ChangeChannelReader.class;
   }

   @Override
   public void handlePacket(ChangeChannelPacket packet, MapleClient client) {
      client.getPlayer().getAutobanManager().setTimestamp(6, Server.getInstance().getCurrentTimestamp(), 3);
      if (client.getChannel() == packet.channel()) {
         AutobanFactory.GENERAL.alert(client.getPlayer(), "CCing to same channel.");
         client.disconnect(false, false);
         return;
      } else if (client.getPlayer().getCashShop().isOpened() || client.getPlayer().getMiniGame() != null || client.getPlayer().getPlayerShop() != null) {
         return;
      }

      client.changeChannel(packet.channel());
   }
}