package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import config.YamlConfig;
import constants.MapleInventoryType;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.InventoryMergePacket;
import net.server.channel.packet.reader.InventoryMergeReader;
import server.MapleItemInformationProvider;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;
import tools.packet.ui.FinishedSort;

public final class InventoryMergeHandler extends AbstractPacketHandler<InventoryMergePacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.getAutoBanManager().setTimestamp(2, Server.getInstance().getCurrentTimestamp(), 4);

      if (!YamlConfig.config.server.USE_ITEM_SORT) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public Class<InventoryMergeReader> getReaderClass() {
      return InventoryMergeReader.class;
   }

   @Override
   public void handlePacket(InventoryMergePacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      if (packet.inventoryType() < 1 || packet.inventoryType() > 5) {
         client.disconnect(false, false);
         return;
      }

      MapleInventoryType inventoryType = MapleInventoryType.getByType(packet.inventoryType());
      MapleInventory inventory = client.getPlayer().getInventory(inventoryType);
      inventory.lockInventory();
      try {
         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         Item srcItem, dstItem;

         for (short dst = 1; dst <= inventory.getSlotLimit(); dst++) {
            dstItem = inventory.getItem(dst);
            if (dstItem == null) {
               continue;
            }

            for (short src = (short) (dst + 1); src <= inventory.getSlotLimit(); src++) {
               srcItem = inventory.getItem(src);
               if (srcItem == null) {
                  continue;
               }

               if (dstItem.id() != srcItem.id()) {
                  continue;
               }
               if (dstItem.quantity() == ii.getSlotMax(client, inventory.getItem(dst).id())) {
                  break;
               }

               MapleInventoryManipulator.move(client, inventoryType, src, dst);
            }
         }

         //------------------------------------------------------------

         inventory = client.getPlayer().getInventory(inventoryType);
         boolean sorted = false;

         while (!sorted) {
            short freeSlot = inventory.getNextFreeSlot();

            if (freeSlot != -1) {
               short itemSlot = -1;
               for (short i = (short) (freeSlot + 1); i <= inventory.getSlotLimit(); i = (short) (i + 1)) {
                  if (inventory.getItem(i) != null) {
                     itemSlot = i;
                     break;
                  }
               }
               if (itemSlot > 0) {
                  MapleInventoryManipulator.move(client, inventoryType, itemSlot, freeSlot);
               } else {
                  sorted = true;
               }
            } else {
               sorted = true;
            }
         }
      } finally {
         inventory.unlockInventory();
      }

      PacketCreator.announce(client, new FinishedSort(inventoryType.getType()));
      PacketCreator.announce(client, new EnableActions());
   }
}