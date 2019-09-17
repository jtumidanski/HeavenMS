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
import client.autoban.AutobanFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.StorageReader;
import net.server.channel.packet.storage.ArrangeItemsPacket;
import net.server.channel.packet.storage.BaseStoragePacket;
import net.server.channel.packet.storage.ClosePacket;
import net.server.channel.packet.storage.MesoPacket;
import net.server.channel.packet.storage.StorePacket;
import net.server.channel.packet.storage.TakeoutPacket;
import server.MapleItemInformationProvider;
import server.MapleStorage;
import tools.FilePrinter;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.ServerNoticeType;

/**
 * @author Matze
 */
public final class StorageHandler extends AbstractPacketHandler<BaseStoragePacket> {
   @Override
   public Class<StorageReader> getReaderClass() {
      return StorageReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.getLevel() < 15) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.POP_UP, "You may only use the storage once you have reached level 15.");
         client.announce(MaplePacketCreator.enableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(BaseStoragePacket packet, MapleClient client) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      MapleCharacter chr = client.getPlayer();
      MapleStorage storage = chr.getStorage();

      if (client.tryAcquireClient()) {
         try {
            if (packet instanceof TakeoutPacket) {
               takeOut(client, ii, chr, storage, ((TakeoutPacket) packet).theType(), ((TakeoutPacket) packet).slot());
            } else if (packet instanceof StorePacket) {
               store(client, ii, chr, storage, ((StorePacket) packet).slot(), ((StorePacket) packet).itemId(), ((StorePacket) packet).quantity());
            } else if (packet instanceof ArrangeItemsPacket) {
               arrangeItems(client, storage);
            } else if (packet instanceof MesoPacket) {
               meso(client, chr, storage, ((MesoPacket) packet).mesos());
            } else if (packet instanceof ClosePacket) {
               close(storage);
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   private void close(MapleStorage storage) {
      storage.close();
   }

   private void meso(MapleClient c, MapleCharacter chr, MapleStorage storage, int meso) {
      int storageMesos = storage.getMeso();
      int playerMesos = chr.getMeso();
      if ((meso > 0 && storageMesos >= meso) || (meso < 0 && playerMesos >= -meso)) {
         if (meso < 0 && (storageMesos - meso) < 0) {
            meso = Integer.MIN_VALUE + storageMesos;
            if (meso < playerMesos) {
               c.announce(MaplePacketCreator.enableActions());
               return;
            }
         } else if (meso > 0 && (playerMesos + meso) < 0) {
            meso = Integer.MAX_VALUE - playerMesos;
            if (meso > storageMesos) {
               c.announce(MaplePacketCreator.enableActions());
               return;
            }
         }
         storage.setMeso(storageMesos - meso);
         chr.gainMeso(meso, false, true, false);
         chr.setUsedStorage();
         FilePrinter.print(FilePrinter.STORAGE + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + (meso > 0 ? " took out " : " stored ") + Math.abs(meso) + " mesos");
         storage.sendMeso(c);
      } else {
         c.announce(MaplePacketCreator.enableActions());
      }
   }

   private void arrangeItems(MapleClient c, MapleStorage storage) {
      if (ServerConstants.USE_STORAGE_ITEM_SORT) {
         storage.arrangeItems(c);
      }
      c.announce(MaplePacketCreator.enableActions());
   }

   private void store(MapleClient c, MapleItemInformationProvider ii, MapleCharacter chr, MapleStorage storage, short slot, int itemId, short quantity) {
      MapleInventoryType invType = ItemConstants.getInventoryType(itemId);
      MapleInventory inv = chr.getInventory(invType);
      if (slot < 1 || slot > inv.getSlotLimit()) { //player inv starts at one
         AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with storage.");
         FilePrinter.print(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to store item at slot " + slot);
         c.disconnect(true, false);
         return;
      }
      if (quantity < 1) {
         c.announce(MaplePacketCreator.enableActions());
         return;
      }
      if (storage.isFull()) {
         c.announce(MaplePacketCreator.getStorageError((byte) 0x11));
         return;
      }

      int storeFee = storage.getStoreFee();
      if (chr.getMeso() < storeFee) {
         c.announce(MaplePacketCreator.getStorageError((byte) 0x0B));
      } else {
         Item item;

         inv.lockInventory();    // thanks imbee for pointing a dupe within storage
         try {
            item = inv.getItem(slot);
            if (item != null && item.getItemId() == itemId && (item.getQuantity() >= quantity || ItemConstants.isRechargeable(itemId))) {
               if (ItemConstants.isWeddingRing(itemId) || ItemConstants.isWeddingToken(itemId)) {
                  c.announce(MaplePacketCreator.enableActions());
                  return;
               }

               if (ItemConstants.isRechargeable(itemId)) {
                  quantity = item.getQuantity();
               }

               MapleInventoryManipulator.removeFromSlot(c, invType, slot, quantity, false);
            } else {
               c.announce(MaplePacketCreator.enableActions());
               return;
            }

            item = item.copy();     // thanks Robin Schulz & BHB88 for noticing a inventory glitch when storing items
         } finally {
            inv.unlockInventory();
         }

         chr.gainMeso(-storeFee, false, true, false);

         MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
         item.setQuantity(quantity);
         storage.store(item);    // inside a critical section, "!(storage.isFull())" is still in effect...
         chr.setUsedStorage();
         String itemName = ii.getName(item.getItemId());
         FilePrinter.print(FilePrinter.STORAGE + c.getAccountName() + ".txt", c.getPlayer().getName() + " stored " + item.getQuantity() + " " + itemName + " (" + item.getItemId() + ")");
         storage.sendStored(c, ItemConstants.getInventoryType(itemId));
      }
   }

   private void takeOut(MapleClient c, MapleItemInformationProvider ii, MapleCharacter chr, MapleStorage storage, byte type, byte slot) {
      if (slot < 0 || slot > storage.getSlots()) { // removal starts at zero
         AutobanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with storage.");
         FilePrinter.print(FilePrinter.EXPLOITS + c.getPlayer().getName() + ".txt", c.getPlayer().getName() + " tried to work with storage slot " + slot);
         c.disconnect(true, false);
         return;
      }
      slot = storage.getSlot(MapleInventoryType.getByType(type), slot);
      Item item = storage.getItem(slot);
      if (item != null) {
         if (ii.isPickupRestricted(item.getItemId()) && chr.haveItemWithId(item.getItemId(), true)) {
            c.announce(MaplePacketCreator.getStorageError((byte) 0x0C));
            return;
         }

         int takeoutFee = storage.getTakeOutFee();
         if (chr.getMeso() < takeoutFee) {
            c.announce(MaplePacketCreator.getStorageError((byte) 0x0B));
            return;
         } else {
            chr.gainMeso(-takeoutFee, false);
         }

         if (MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
            if (storage.takeOut(item)) {
               chr.setUsedStorage();

               MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
               MapleInventoryManipulator.addFromDrop(c, item, false);

               String itemName = ii.getName(item.getItemId());
               FilePrinter.print(FilePrinter.STORAGE + c.getAccountName() + ".txt", c.getPlayer().getName() + " took out " + item.getQuantity() + " " + itemName + " (" + item.getItemId() + ")");

               storage.sendTakenOut(c, item.getInventoryType());
            } else {
               c.announce(MaplePacketCreator.enableActions());
            }
         } else {
            c.announce(MaplePacketCreator.getStorageError((byte) 0x0A));
         }
      }
   }
}