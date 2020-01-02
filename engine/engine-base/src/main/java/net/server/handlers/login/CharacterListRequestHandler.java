package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.Channel;
import net.server.channel.packet.reader.CharacterListRequestReader;
import net.server.login.packet.CharacterListRequestPacket;
import net.server.world.World;
import tools.PacketCreator;
import tools.packet.character.CharacterList;
import tools.packet.serverlist.GetServerStatus;
import tools.packet.serverlist.ServerStatus;

public final class CharacterListRequestHandler extends AbstractPacketHandler<CharacterListRequestPacket> {
   @Override
   public Class<CharacterListRequestReader> getReaderClass() {
      return CharacterListRequestReader.class;
   }

   @Override
   public void handlePacket(CharacterListRequestPacket packet, MapleClient client) {
      World world = Server.getInstance().getWorld(packet.world());
      if (world == null || world.isWorldCapacityFull()) {
         PacketCreator.announce(client, new GetServerStatus(ServerStatus.FULL));
         return;
      }

      Channel channel = world.getChannel(packet.channel());
      if (channel == null) {
         PacketCreator.announce(client, new GetServerStatus(ServerStatus.FULL));
         return;
      }

      client.setWorld(packet.world());
      client.setChannel(packet.channel());
      PacketCreator.announce(client, new CharacterList(client, packet.world(), 0));
   }
}