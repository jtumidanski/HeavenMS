package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.packet.cashshop.ShowCash;

public final class TouchingCashShopHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      PacketCreator.announce(client, new ShowCash(chr.getCashShop().getCash(1), chr.getCashShop().getCash(2), chr.getCashShop().getCash(4)));
   }
}
