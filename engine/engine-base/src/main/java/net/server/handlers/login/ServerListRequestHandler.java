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

import java.util.List;
import java.util.stream.Collectors;

import client.MapleClient;
import constants.game.GameConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import net.server.world.World;
import tools.PacketCreator;
import tools.packet.ChannelLoad;
import tools.packet.RecommendedWorldMessage;
import tools.packet.SelectWorld;
import tools.packet.serverlist.ServerList;
import tools.packet.serverlist.ServerListEnd;

public final class ServerListRequestHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      Server server = Server.getInstance();
      List<World> worlds = server.getWorlds();
      client.requestedServerList(worlds.size());

      for (World world : worlds) {
         List<ChannelLoad> loadData = world.getChannels().stream().map(channel -> new ChannelLoad(channel.getId(), channel.getChannelCapacity())).collect(Collectors.toList());
         ServerList serverListData = new ServerList(world.getId(), GameConstants.WORLD_NAMES[world.getId()], world.getFlag(), world.getEventMessage(), loadData);
         PacketCreator.announce(client, serverListData);
      }
      PacketCreator.announce(client, new ServerListEnd());
      PacketCreator.announce(client, new SelectWorld(0));
      PacketCreator.announce(client, new RecommendedWorldMessage(server.worldRecommendedList()));
   }
}