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
package server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import javax.persistence.EntityManager;

import client.MapleClient;
import database.administrator.StorageAdministrator;
import database.provider.StorageProvider;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import database.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.Pair;
import tools.ServerNoticeType;
import tools.packet.stat.EnableActions;
import tools.packet.storage.ArrangeStorage;
import tools.packet.storage.GetStorage;
import tools.packet.storage.MesoStorage;
import tools.packet.storage.StoreInStorage;
import tools.packet.storage.TakeOutOfStorage;

/**
 * @author Matze
 */
public class MapleStorage {
   private static Map<Integer, Integer> trunkGetCache = new HashMap<>();
   private static Map<Integer, Integer> trunkPutCache = new HashMap<>();

   private int id;
   private int currentNpcid;
   private int meso;
   private byte slots;
   private Map<MapleInventoryType, List<Item>> typeItems = new HashMap<>();
   private List<Item> items = new LinkedList<>();
   private Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.STORAGE, true);

   public MapleStorage(int id, byte slots, int meso) {
      this.id = id;
      this.slots = slots;
      this.meso = meso;
   }

   private static MapleStorage create(EntityManager entityManager, int id, int world) {
      StorageAdministrator.getInstance().create(entityManager, id, world);
      Optional<MapleStorage> mapleStorage = StorageProvider.getInstance().getByAccountAndWorld(entityManager, id, world);
      return mapleStorage.map(MapleStorage::loadItemsForStorage).orElseThrow();
   }

   private static MapleStorage loadItemsForStorage(MapleStorage mapleStorage) {
      for (Pair<Item, MapleInventoryType> item : ItemFactory.STORAGE.loadItems(mapleStorage.id, false)) {
         mapleStorage.items.add(item.getLeft());
      }
      return mapleStorage;
   }

   public static MapleStorage loadOrCreateFromDB(int id, int world) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<MapleStorage> mapleStorage = StorageProvider.getInstance().getByAccountAndWorld(connection, id, world);
         return mapleStorage.map(MapleStorage::loadItemsForStorage).orElseGet(() -> create(connection, id, world));
      }).orElseThrow();
   }

   public byte getSlots() {
      return slots;
   }

   public synchronized boolean gainSlots(int slots) {
      lock.lock();
      try {
         slots += this.slots;

         if (slots <= 48) {
            this.slots = (byte) slots;
            return true;
         }

         return false;
      } finally {
         lock.unlock();
      }
   }

   public void saveToDB(EntityManager entityManager) {
      StorageAdministrator.getInstance().update(entityManager, id, slots, meso);

      List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

      List<Item> list = getItems();
      for (Item item : list) {
         itemsWithType.add(new Pair<>(item, item.inventoryType()));
      }

      ItemFactory.STORAGE.saveItems(itemsWithType, id, entityManager);
   }

   public Item getItem(byte slot) {
      lock.lock();
      try {
         return items.get(slot);
      } finally {
         lock.unlock();
      }
   }

   public boolean takeOut(Item item) {
      lock.lock();
      try {
         boolean ret = items.remove(item);

         MapleInventoryType type = item.inventoryType();
         typeItems.put(type, new ArrayList<>(filterItems(type)));

         return ret;
      } finally {
         lock.unlock();
      }
   }

   public boolean store(Item item) {
      lock.lock();
      try {
         if (isFull()) { // thanks Optimist for noticing unrestricted amount of insertions here
            return false;
         }

         items.add(item);

         MapleInventoryType type = item.inventoryType();
         typeItems.put(type, new ArrayList<>(filterItems(type)));

         return true;
      } finally {
         lock.unlock();
      }
   }

   public List<Item> getItems() {
      lock.lock();
      try {
         return Collections.unmodifiableList(items);
      } finally {
         lock.unlock();
      }
   }

   private List<Item> filterItems(MapleInventoryType type) {
      List<Item> storageItems = getItems();
      List<Item> ret = new LinkedList<>();

      for (Item item : storageItems) {
         if (item.inventoryType() == type) {
            ret.add(item);
         }
      }
      return ret;
   }

   public byte getSlot(MapleInventoryType type, byte slot) {
      lock.lock();
      try {
         byte ret = 0;
         List<Item> storageItems = getItems();
         for (Item item : storageItems) {
            if (item == typeItems.get(type).get(slot)) {
               return ret;
            }
            ret++;
         }
         return -1;
      } finally {
         lock.unlock();
      }
   }

   public void sendStorage(MapleClient c, int npcId) {
      if (c.getPlayer().getLevel() < 15) {
         MessageBroadcaster.getInstance().sendServerNotice(c.getPlayer(), ServerNoticeType.POP_UP, "You may only use the storage once you have reached level 15.");
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      lock.lock();
      try {
         items.sort(new Comparator<>() {
            @Override
            public int compare(Item o1, Item o2) {
               if (o1.inventoryType().getType() < o2.inventoryType().getType()) {
                  return -1;
               } else if (o1.inventoryType() == o2.inventoryType()) {
                  return 0;
               }
               return 1;
            }
         });

         List<Item> storageItems = getItems();
         for (MapleInventoryType type : MapleInventoryType.values()) {
            typeItems.put(type, new ArrayList<>(storageItems));
         }

         currentNpcid = npcId;
         PacketCreator.announce(c, new GetStorage(npcId, slots, storageItems, meso));
      } finally {
         lock.unlock();
      }
   }

   public void sendStored(MapleClient c, MapleInventoryType type) {
      lock.lock();
      try {
         PacketCreator.announce(c, new StoreInStorage(slots, type, typeItems.get(type)));
      } finally {
         lock.unlock();
      }
   }

   public void sendTakenOut(MapleClient c, MapleInventoryType type) {
      lock.lock();
      try {
         PacketCreator.announce(c, new TakeOutOfStorage(slots, type, typeItems.get(type)));
      } finally {
         lock.unlock();
      }
   }

   public void arrangeItems(MapleClient c) {
      lock.lock();
      try {
         MapleStorageInventory msi = new MapleStorageInventory(c, items);
         msi.mergeItems();
         items = msi.sortItems();

         for (MapleInventoryType type : MapleInventoryType.values()) {
            typeItems.put(type, new ArrayList<>(items));
         }

         PacketCreator.announce(c, new ArrangeStorage(slots, items));
      } finally {
         lock.unlock();
      }
   }

   public int getMeso() {
      return meso;
   }

   public void setMeso(int meso) {
      if (meso < 0) {
         throw new RuntimeException();
      }
      this.meso = meso;
   }

   public void sendMeso(MapleClient c) {
      PacketCreator.announce(c, new MesoStorage(slots, meso));
   }

   public int getStoreFee() {  // thanks to GabrielSin
      int npcId = currentNpcid;
      Integer fee = trunkPutCache.get(npcId);
      if (fee == null) {
         fee = 100;

         MapleDataProvider npc = MapleDataProviderFactory.getDataProvider(new File("wz/Npc.wz"));
         MapleData npcData = npc.getData(npcId + ".img");
         if (npcData != null) {
            fee = MapleDataTool.getIntConvert("info/trunkPut", npcData, 100);
         }

         trunkPutCache.put(npcId, fee);
      }

      return fee;
   }

   public int getTakeOutFee() {
      int npcId = currentNpcid;
      Integer fee = trunkGetCache.get(npcId);
      if (fee == null) {
         fee = 0;

         MapleDataProvider npc = MapleDataProviderFactory.getDataProvider(new File("wz/Npc.wz"));
         MapleData npcData = npc.getData(npcId + ".img");
         if (npcData != null) {
            fee = MapleDataTool.getIntConvert("info/trunkGet", npcData, 0);
         }

         trunkGetCache.put(npcId, fee);
      }

      return fee;
   }

   public boolean isFull() {
      lock.lock();
      try {
         return items.size() >= slots;
      } finally {
         lock.unlock();
      }
   }

   public void close() {
      lock.lock();
      try {
         typeItems.clear();
      } finally {
         lock.unlock();
      }
   }
}