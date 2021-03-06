package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.inventory.manipulator.MapleKarmaManipulator;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import constants.ItemConstants;
import constants.MapleInventoryType;
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
import tools.I18nMessage;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.storage.StorageError;

public final class StorageHandler extends AbstractPacketHandler<BaseStoragePacket> {
   @Override
   public Class<StorageReader> getReaderClass() {
      return StorageReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.getLevel() < 15) {
         MessageBroadcaster.getInstance()
               .sendServerNotice(chr, ServerNoticeType.POP_UP, I18nMessage.from("STORAGE_LEVEL_REQUIREMENT"));
         PacketCreator.announce(client, new EnableActions());
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
               store(client, ii, chr, storage, ((StorePacket) packet).slot(), ((StorePacket) packet).itemId(),
                     ((StorePacket) packet).quantity());
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
               PacketCreator.announce(c, new EnableActions());
               return;
            }
         } else if (meso > 0 && (playerMesos + meso) < 0) {
            meso = Integer.MAX_VALUE - playerMesos;
            if (meso > storageMesos) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }
         }
         storage.setMeso(storageMesos - meso);
         chr.gainMeso(meso, false, true, false);
         chr.setUsedStorage();
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.STORAGE,
               c.getPlayer().getName() + (meso > 0 ? " took out " : " stored ") + Math.abs(meso) + " mesos");
         storage.sendMeso(c);
      } else {
         PacketCreator.announce(c, new EnableActions());
      }
   }

   private void arrangeItems(MapleClient c, MapleStorage storage) {
      if (YamlConfig.config.server.USE_STORAGE_ITEM_SORT) {
         storage.arrangeItems(c);
      }
      PacketCreator.announce(c, new EnableActions());
   }

   private void store(MapleClient c, MapleItemInformationProvider ii, MapleCharacter chr, MapleStorage storage, short slot,
                      int itemId, short quantity) {
      MapleInventoryType invType = ItemConstants.getInventoryType(itemId);
      MapleInventory inv = chr.getInventory(invType);
      if (slot < 1 || slot > inv.getSlotLimit()) { //player inv starts at one
         AutoBanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with storage.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS,
               c.getPlayer().getName() + " tried to store item at slot " + slot);
         c.disconnect(true, false);
         return;
      }
      if (quantity < 1) {
         PacketCreator.announce(c, new EnableActions());
         return;
      }
      if (storage.isFull()) {
         PacketCreator.announce(c, new StorageError((byte) 0x11));
         return;
      }

      int storeFee = storage.getStoreFee();
      if (chr.getMeso() < storeFee) {
         PacketCreator.announce(c, new StorageError((byte) 0x0B));
      } else {
         Item item;

         inv.lockInventory();
         try {
            item = inv.getItem(slot);
            if (item != null && item.id() == itemId && (item.quantity() >= quantity || ItemConstants.isRechargeable(itemId))) {
               if (ItemConstants.isWeddingRing(itemId) || ItemConstants.isWeddingToken(itemId)) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }

               if (ItemConstants.isRechargeable(itemId)) {
                  quantity = item.quantity();
               }

               MapleInventoryManipulator.removeFromSlot(c, invType, slot, quantity, false);
            } else {
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            item = item.copy();
         } finally {
            inv.unlockInventory();
         }

         chr.gainMeso(-storeFee, false, true, false);

         item = MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
         item = item.setQuantity(quantity);
         storage.store(item);    // inside a critical section, "!(storage.isFull())" is still in effect...
         chr.setUsedStorage();
         String itemName = ii.getName(item.id());
         LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.STORAGE,
               c.getPlayer().getName() + " stored " + item.quantity() + " " + itemName + " (" + item.id() + ")");
         storage.sendStored(c, ItemConstants.getInventoryType(itemId));
      }
   }

   private void takeOut(MapleClient c, MapleItemInformationProvider ii, MapleCharacter chr, MapleStorage storage, byte type,
                        byte slot) {
      if (slot < 0 || slot > storage.getSlots()) { // removal starts at zero
         AutoBanFactory.PACKET_EDIT.alert(c.getPlayer(), c.getPlayer().getName() + " tried to packet edit with storage.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS,
               c.getPlayer().getName() + " tried to work with storage slot " + slot);
         c.disconnect(true, false);
         return;
      }
      slot = storage.getSlot(MapleInventoryType.getByType(type), slot);
      Item item = storage.getItem(slot);
      if (item != null) {
         if (ii.isPickupRestricted(item.id()) && chr.haveItemWithId(item.id(), true)) {
            PacketCreator.announce(c, new StorageError((byte) 0x0C));
            return;
         }

         int takeoutFee = storage.getTakeOutFee();
         if (chr.getMeso() < takeoutFee) {
            PacketCreator.announce(c, new StorageError((byte) 0x0B));
            return;
         } else {
            chr.gainMeso(-takeoutFee, false);
         }

         if (MapleInventoryManipulator.checkSpace(c, item.id(), item.quantity(), item.owner())) {
            if (storage.takeOut(item)) {
               chr.setUsedStorage();

               item = MapleKarmaManipulator.toggleKarmaFlagToUntradeable(item);
               MapleInventoryManipulator.addFromDrop(c, item, false);

               String itemName = ii.getName(item.id());
               LoggerUtil.printInfo(LoggerOriginator.ENGINE, LogType.STORAGE,
                     c.getPlayer().getName() + " took out " + item.quantity() + " " + itemName + " (" + item.id() + ")");

               storage.sendTakenOut(c, item.inventoryType());
            } else {
               PacketCreator.announce(c, new EnableActions());
            }
         } else {
            PacketCreator.announce(c, new StorageError((byte) 0x0A));
         }
      }
   }
}