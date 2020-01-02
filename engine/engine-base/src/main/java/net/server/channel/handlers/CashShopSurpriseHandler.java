package net.server.channel.handlers;

import client.MapleClient;
import client.inventory.Item;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import server.CashShop;
import tools.PacketCreator;
import tools.Pair;
import tools.packet.cashshop.gachapon.CashShopGachaponFailed;
import tools.packet.cashshop.gachapon.CashShopGachaponSuccess;

public class CashShopSurpriseHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      CashShop cs = client.getPlayer().getCashShop();

      if (cs.isOpened()) {
         Pair<Item, Item> cssResult = cs.openCashShopSurprise();

         if (cssResult != null) {
            Item cssItem = cssResult.getLeft(), cssBox = cssResult.getRight();
            PacketCreator.announce(client, new CashShopGachaponSuccess(client.getAccID(), cssBox.sn(), cssBox.quantity(), cssItem, cssItem.id(), cssItem.quantity(), true));
         } else {
            PacketCreator.announce(client, new CashShopGachaponFailed());
         }
      }
   }
}
