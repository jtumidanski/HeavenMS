package net.server.handlers.login;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.packet.RelogResponse;

public final class RelogRequestHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      PacketCreator.announce(client, new RelogResponse());
   }

   @Override
   public boolean validateState(MapleClient client) {
      return !client.isLoggedIn();
   }
}
