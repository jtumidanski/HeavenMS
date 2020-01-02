package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.MasterBroadcaster;
import tools.packet.character.box.UseChalkboard;

public final class CloseChalkboardHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      client.getPlayer().setChalkboard(null);
      MasterBroadcaster.getInstance().sendToAllInMap(client.getPlayer().getMap(), new UseChalkboard(client.getPlayer().getId(), true, client.getPlayer().getChalkboard()));
   }
}
