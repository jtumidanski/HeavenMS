package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.reader.ServerStatusRequestReader;
import net.server.login.packet.ServerStatusRequestPacket;
import net.server.world.World;
import tools.PacketCreator;
import tools.packet.serverlist.GetServerStatus;
import tools.packet.serverlist.ServerStatus;

public final class ServerStatusRequestHandler extends AbstractPacketHandler<ServerStatusRequestPacket> {
   @Override
   public Class<ServerStatusRequestReader> getReaderClass() {
      return ServerStatusRequestReader.class;
   }

   @Override
   public void handlePacket(ServerStatusRequestPacket packet, MapleClient client) {
      World world = Server.getInstance().getWorld(packet.world());
      if (world != null) {
         int status = world.getWorldCapacityStatus();
         PacketCreator.announce(client, new GetServerStatus(status));
      } else {
         PacketCreator.announce(client, new GetServerStatus(ServerStatus.FULL));
      }
   }
}
