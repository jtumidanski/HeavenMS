/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.inventory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import client.database.administrator.InventoryEquipmentAdministrator;
import client.database.administrator.InventoryItemAdministrator;
import client.database.provider.InventoryItemProvider;
import client.database.administrator.InventoryMerchantAdministrator;
import client.database.provider.InventoryMerchantProvider;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import tools.DatabaseConnection;
import tools.Pair;

/**
 * @author Flav
 */
public enum ItemFactory {

   INVENTORY(1, false),
   STORAGE(2, true),
   CASH_EXPLORER(3, true),
   CASH_CYGNUS(4, true),
   CASH_ARAN(5, true),
   MERCHANT(6, false),
   CASH_OVERALL(7, true),
   MARRIAGE_GIFTS(8, false),
   DUEY(9, false);
   private static final int lockCount = 400;
   private static final Lock[] locks = new Lock[lockCount];  // thanks Masterrulax for pointing out a bottleneck issue here

   static {
      for (int i = 0; i < lockCount; i++) {
         locks[i] = MonitoredReentrantLockFactory.createLock(MonitoredLockType.ITEM, true);
      }
   }

   private final int value;
   private final boolean account;

   ItemFactory(int value, boolean account) {
      this.value = value;
      this.account = account;
   }

   public static List<Pair<Item, Integer>> loadEquippedItems(int id, boolean isAccount, boolean login) {
      if (isAccount) {
         return DatabaseConnection.withConnectionResult(connection -> InventoryItemProvider.getInstance().getEquipsByAccount(connection, id, login)).orElse(new ArrayList<>());
      } else {
         return DatabaseConnection.withConnectionResult(connection -> InventoryItemProvider.getInstance().getEquipsByCharacter(connection, id, login)).orElse(new ArrayList<>());
      }
   }

   public int getValue() {
      return value;
   }

   public List<Pair<Item, MapleInventoryType>> loadItems(int id, boolean login) {
      if (value != 6) {
         return loadItemsCommon(id, login);
      } else {
         return loadItemsMerchant(id, login);
      }
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, int id, Connection con) {
      saveItems(items, null, id, con);
   }

   public void saveItems(List<Pair<Item, MapleInventoryType>> items, List<Short> bundlesList, int id, Connection con) {
      // thanks Arufonsu, MedicOP, BHB for pointing a "synchronized" bottleneck here

      if (value != 6) {
         saveItemsCommon(items, id, con);
      } else {
         saveItemsMerchant(items, bundlesList, id, con);
      }
   }

   private List<Pair<Item, MapleInventoryType>> loadItemsCommon(int id, boolean login) {
      if (account) {
         return DatabaseConnection.withConnectionResult(connection -> InventoryItemProvider.getInstance().getItemsByAccountAndType(connection, id, value, login)).orElse(new ArrayList<>());
      } else {
         return DatabaseConnection.withConnectionResult(connection -> InventoryItemProvider.getInstance().getItemsByCharacterAndType(connection, id, value, login)).orElse(new ArrayList<>());
      }
   }

   private void saveItemsCommon(List<Pair<Item, MapleInventoryType>> items, int id, Connection con) {
      Lock lock = locks[id % lockCount];
      lock.lock();
      try {
         if (account) {
            InventoryItemAdministrator.getInstance().deleteForAccountByType(con, id, value);
         } else {
            InventoryItemAdministrator.getInstance().deleteForCharacterByType(con, id, value);
         }


         if (!items.isEmpty()) {
            for (Pair<Item, MapleInventoryType> pair : items) {
               Item item = pair.getLeft();
               MapleInventoryType mit = pair.getRight();
               int genKey = InventoryItemAdministrator.getInstance().create(con, value, account ? -1 : id, account ? id : -1,
                     item.getItemId(), mit.getType(), item.getPosition(), item.getQuantity(), item.getOwner(),
                     item.getPetId(), item.getFlag(), item.getExpiration(), item.getGiftFrom());

               if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                  saveEquipItem(con, (Equip) item, genKey);
               }
            }
         }
      } finally {
         lock.unlock();
      }
   }

   private List<Pair<Item, MapleInventoryType>> loadItemsMerchant(int id, boolean login) {
      return DatabaseConnection.withConnectionResult(connection -> {
         List<Pair<Item, MapleInventoryType>> results;
         if (account) {
            results = InventoryItemProvider.getInstance().getItemsByAccountAndType(connection, id, value, login);
         } else {
            results = InventoryItemProvider.getInstance().getItemsByCharacterAndType(connection, id, value, login);
         }
         return results.stream().map(original -> {
            short bundles = InventoryMerchantProvider.getInstance().getBundleForItem(connection, original.getLeft().getItemId());
            MapleInventoryType inventoryType = original.getRight();
            if (inventoryType.equals(MapleInventoryType.EQUIP) || inventoryType.equals(MapleInventoryType.EQUIPPED)) {
               return original;
            } else {
               if (bundles > 0) {
                  original.getLeft().setQuantity((short) (bundles * original.getLeft().getQuantity()));
                  return original;
               }
            }
            return null;
         }).collect(Collectors.toList());
      }).orElse(new ArrayList<>());
   }

   private void saveItemsMerchant(List<Pair<Item, MapleInventoryType>> items, List<Short> bundlesList, int id, Connection con) {
      Lock lock = locks[id % lockCount];
      lock.lock();
      try {
         InventoryMerchantAdministrator.getInstance().deleteForCharacter(con, id);
         if (account) {
            InventoryItemAdministrator.getInstance().deleteForAccountByType(con, id, value);

         } else {
            InventoryItemAdministrator.getInstance().deleteForCharacterByType(con, id, value);
         }


         if (!items.isEmpty()) {
            int i = 0;
            for (Pair<Item, MapleInventoryType> pair : items) {
               Item item = pair.getLeft();
               Short bundles = bundlesList.get(i);
               MapleInventoryType mit = pair.getRight();
               i++;

               int genKey = InventoryItemAdministrator.getInstance().create(con, value, account ? -1 : id,
                     account ? id : -1, item.getItemId(), mit.getType(), item.getPosition(), item.getQuantity(),
                     item.getOwner(), item.getPetId(), item.getFlag(), item.getExpiration(), item.getGiftFrom());
               InventoryMerchantAdministrator.getInstance().create(con, genKey, id, bundles);

               if (mit.equals(MapleInventoryType.EQUIP) || mit.equals(MapleInventoryType.EQUIPPED)) {
                  saveEquipItem(con, (Equip) item, genKey);
               }
            }
         }
      } finally {
         lock.unlock();
      }
   }

   private void saveEquipItem(Connection con, Equip item, int genKey) {
      InventoryEquipmentAdministrator.getInstance().create(con, genKey, item.getUpgradeSlots(),
            item.getLevel(), item.getStr(), item.getDex(), item.getInt(), item.getLuk(),
            item.getHp(), item.getMp(), item.getWatk(), item.getMatk(), item.getWdef(),
            item.getMdef(), item.getAcc(), item.getAvoid(), item.getHands(), item.getSpeed(),
            item.getJump(), 0, item.getVicious(), item.getItemLevel(), item.getItemExp(),
            item.getRingId());
   }
}