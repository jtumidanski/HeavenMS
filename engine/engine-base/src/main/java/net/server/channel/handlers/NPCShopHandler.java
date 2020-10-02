package net.server.channel.handlers;

import client.MapleClient;
import client.autoban.AutoBanFactory;
import constants.inventory.ItemConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.NPCShopPacket;
import net.server.channel.packet.reader.NPCShopReader;
import server.processor.MapleShopProcessor;
import tools.LoggerOriginator;
import tools.LoggerUtil;

public final class NPCShopHandler extends AbstractPacketHandler<NPCShopPacket> {
   @Override
   public Class<NPCShopReader> getReaderClass() {
      return NPCShopReader.class;
   }

   @Override
   public void handlePacket(NPCShopPacket packet, MapleClient client) {
      if (packet.mode() == 0) { // mode 0 = buy :)
         if (packet.quantity() < 1) {
            AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit a npc shop.");
            LoggerUtil.printError(LoggerOriginator.EXPLOITS, client.getPlayer().getName() + " tried to buy quantity " + packet.quantity() + " of item id " + packet.itemId());
            client.disconnect(true, false);
            return;
         }
         MapleShopProcessor.getInstance().buy(client.getPlayer().getShop(), client, packet.slot(), packet.itemId(), packet.quantity());
      } else if (packet.mode() == 1) { // sell ;)
         MapleShopProcessor.getInstance().sell(client, ItemConstants.getInventoryType(packet.itemId()), packet.slot(), packet.quantity());
      } else if (packet.mode() == 2) { // recharge ;)
         MapleShopProcessor.getInstance().recharge(client, packet.slot());
      } else if (packet.mode() == 3) { // leaving :(
         client.getPlayer().setShop(null);
      }
   }
}
