package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import constants.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.FaceExpressionPacket;
import net.server.channel.packet.reader.FaceExpressionReader;

public final class FaceExpressionHandler extends AbstractPacketHandler<FaceExpressionPacket> {
   @Override
   public Class<FaceExpressionReader> getReaderClass() {
      return FaceExpressionReader.class;
   }

   @Override
   public void handlePacket(FaceExpressionPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (packet.emote() > 7) {
         int itemId = 5159992 + packet.emote();
         if (!ItemConstants.isFaceExpression(itemId)
               || chr.getInventory(ItemConstants.getInventoryType(itemId)).findById(itemId) == null) {
            return;
         }
      } else if (packet.emote() < 1) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {   // expecting players never intends to wear the emote 0 (default face, that changes back after 5sec timeout)
            if (chr.isLoggedInWorld()) {
               chr.changeFaceExpression(packet.emote());
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
