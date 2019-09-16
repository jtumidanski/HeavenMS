/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation version 3 as published by
 the Free Software Foundation. You may not use, modify or distribute
 this program under any other version of the GNU Affero General Public
 License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.InventoryMergePacket;
import net.server.channel.packet.reader.InventoryMergeReader;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

public final class InventoryMergeHandler extends AbstractPacketHandler<InventoryMergePacket> {
   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      chr.getAutobanManager().setTimestamp(2, Server.getInstance().getCurrentTimestamp(), 4);

      if (!ServerConstants.USE_ITEM_SORT) {
         client.announce(MaplePacketCreator.enableActions());
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
         //------------------- RonanLana's SLOT MERGER -----------------

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

               if (dstItem.getItemId() != srcItem.getItemId()) {
                  continue;
               }
               if (dstItem.getQuantity() == ii.getSlotMax(client, inventory.getItem(dst).getItemId())) {
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

      client.announce(MaplePacketCreator.finishedSort(inventoryType.getType()));
      client.announce(MaplePacketCreator.enableActions());
   }
}