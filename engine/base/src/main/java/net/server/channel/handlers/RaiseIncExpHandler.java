package net.server.channel.handlers;

import java.util.Map;

import client.MapleClient;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemUIPacket;
import net.server.channel.packet.reader.UseItemUIReader;
import server.MapleItemInformationProvider;
import server.MapleItemInformationProvider.QuestConsItem;

/**
 * @author Xari
 * @author Ronan - added concurrency protection and quest progress limit
 */
public class RaiseIncExpHandler extends AbstractPacketHandler<UseItemUIPacket, UseItemUIReader> {
   @Override
   public Class<UseItemUIReader> getReaderClass() {
      return UseItemUIReader.class;
   }

   @Override
   public void handlePacket(UseItemUIPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            QuestConsItem consItem = ii.getQuestConsumablesInfo(packet.itemId());
            if (consItem == null) {
               return;
            }

            int questid = consItem.questid;
            Map<Integer, Integer> consumables = consItem.items;

            int consId;
            MapleInventory inv = client.getPlayer().getInventory(MapleInventoryType.getByType(packet.inventoryType()));
            inv.lockInventory();
            try {
               consId = inv.getItem(packet.slot()).getItemId();
               if (!consumables.containsKey(consId) || !client.getPlayer().haveItem(consId)) {
                  return;
               }

               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.getByType(packet.inventoryType()), packet.slot(), (short) 1, false, true);
            } finally {
               inv.unlockInventory();
            }

            int nextValue = Math.min(consumables.get(consId) + Integer.parseInt(client.getPlayer().getQuestInfo(questid)), consItem.exp * consItem.grade);
            client.getPlayer().updateQuestInfo(questid, "" + nextValue);
         } finally {
            client.releaseClient();
         }
      }
   }
}
