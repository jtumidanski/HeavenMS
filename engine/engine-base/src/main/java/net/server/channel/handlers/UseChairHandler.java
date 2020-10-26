package net.server.channel.handlers;

import client.MapleClient;
import constants.ItemConstants;
import constants.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseChairPacket;
import net.server.channel.packet.reader.UseChairReader;

public final class UseChairHandler extends AbstractPacketHandler<UseChairPacket> {
   @Override
   public Class<UseChairReader> getReaderClass() {
      return UseChairReader.class;
   }

   @Override
   public void handlePacket(UseChairPacket packet, MapleClient client) {
      int itemId = packet.itemId();
      if (!ItemConstants.isChair(itemId) || client.getPlayer().getInventory(MapleInventoryType.SETUP).findById(itemId) == null) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {
            client.getPlayer().sitChair(itemId);
         } finally {
            client.releaseClient();
         }
      }
   }
}
