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