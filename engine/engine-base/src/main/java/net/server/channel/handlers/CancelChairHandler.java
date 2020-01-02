package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.CancelChairPacket;
import net.server.channel.packet.reader.CancelChairReader;

public final class CancelChairHandler extends AbstractPacketHandler<CancelChairPacket> {
   @Override
   public Class<CancelChairReader> getReaderClass() {
      return CancelChairReader.class;
   }

   @Override
   public void handlePacket(CancelChairPacket packet, MapleClient client) {
      MapleCharacter mc = client.getPlayer();

      if (packet.itemId() >= mc.getMap().getSeats()) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {
            mc.sitChair(packet.itemId());
         } finally {
            client.releaseClient();
         }
      }
   }
}
