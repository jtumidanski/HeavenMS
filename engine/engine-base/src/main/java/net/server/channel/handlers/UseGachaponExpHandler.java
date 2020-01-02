package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutoBanFactory;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public class UseGachaponExpHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            if (client.getPlayer().getGachaponExperience() <= 0) {
               AutoBanFactory.GACHAPON_EXP.autoBan(client.getPlayer(), "Player tried to redeem Gachapon EXP, but had none to redeem.");
            }
            client.getPlayer().gainGachaponExp();
         } finally {
            client.releaseClient();
         }
      }

      PacketCreator.announce(client, new EnableActions());
   }
}
