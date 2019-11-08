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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

import javax.persistence.EntityManager;

import client.database.administrator.AccountAdministrator;
import client.database.administrator.GiftAdministrator;
import client.database.administrator.WishListAdministrator;
import client.database.data.AccountCashShopData;
import client.database.data.GiftData;
import client.database.provider.AccountProvider;
import client.database.provider.GiftProvider;
import client.database.provider.SpecialCashItemProvider;
import client.database.provider.WishListProvider;
import client.inventory.BetterItemFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventoryType;
import client.processor.PetProcessor;
import constants.ItemConstants;
import constants.ServerConstants;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.DatabaseConnection;
import tools.Pair;
import tools.packet.Gift;

/*
 * @author Flav
 */
public class CashShop {
   private int accountId, characterId, nxCredit, maplePoint, nxPrepaid;
   private boolean opened;
   private ItemFactory factory;
   private List<Item> inventory = new ArrayList<>();
   private List<Integer> wishList = new ArrayList<>();
   private int notes = 0;
   private Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CASHSHOP);

   public CashShop(int accountId, int characterId, int jobType) {
      this.accountId = accountId;
      this.characterId = characterId;

      if (!ServerConstants.USE_JOINT_CASHSHOP_INVENTORY) {
         if (jobType == 0) {
            factory = ItemFactory.CASH_EXPLORER;
         } else if (jobType == 1) {
            factory = ItemFactory.CASH_CYGNUS;
         } else if (jobType == 2) {
            factory = ItemFactory.CASH_ARAN;
         }
      } else {
         factory = ItemFactory.CASH_OVERALL;
      }

      DatabaseConnection.getInstance().withConnection(connection -> {
         AccountCashShopData cashShopData = AccountProvider.getInstance().getAccountCashShopData(connection, accountId);
         this.nxCredit = cashShopData.nxCredit();
         this.maplePoint = cashShopData.maplePoint();
         this.nxPrepaid = cashShopData.nxPrepaid();

         for (Pair<Item, MapleInventoryType> item : factory.loadItems(accountId, false)) {
            inventory.add(item.getLeft());
         }

         wishList.addAll(WishListProvider.getInstance().getWishListSn(connection, characterId));

      });
   }

   public static Item generateCouponItem(int itemId, short quantity) {
      CashItem it = new CashItem(77777777, itemId, 7777, ItemConstants.isPet(itemId) ? 30 : 0, quantity, true);
      return it.toItem();
   }

   public int getCash(int type) {
      switch (type) {
         case 1:
            return nxCredit;
         case 2:
            return maplePoint;
         case 4:
            return nxPrepaid;
      }

      return 0;
   }

   public void gainCash(int type, int cash) {
      switch (type) {
         case 1:
            nxCredit += cash;
            break;
         case 2:
            maplePoint += cash;
            break;
         case 4:
            nxPrepaid += cash;
            break;
      }
   }

   public void gainCash(int type, CashItem buyItem, int world) {
      gainCash(type, -buyItem.getPrice());
      if (!ServerConstants.USE_ENFORCE_ITEM_SUGGESTION) {
         Server.getInstance().getWorld(world).addCashItemBought(buyItem.getSN());
      }
   }

   public boolean isOpened() {
      return opened;
   }

   public void open(boolean b) {
      opened = b;
   }

   public List<Item> getInventory() {
      lock.lock();
      try {
         return Collections.unmodifiableList(inventory);
      } finally {
         lock.unlock();
      }
   }

   public Item findByCashId(int cashId) {
      boolean isRing;
      Equip equip = null;
      for (Item item : getInventory()) {
         if (item.inventoryType().equals(MapleInventoryType.EQUIP)) {
            equip = (Equip) item;
            isRing = equip.ringId() > -1;
         } else {
            isRing = false;
         }

         if ((item.petId() > -1 ? item.petId() : isRing ? equip.ringId() : item.cashId()) == cashId) {
            return item;
         }
      }

      return null;
   }

   public void addToInventory(Item item) {
      lock.lock();
      try {
         inventory.add(item);
      } finally {
         lock.unlock();
      }
   }

   public void removeFromInventory(Item item) {
      lock.lock();
      try {
         inventory.remove(item);
      } finally {
         lock.unlock();
      }
   }

   public List<Integer> getWishList() {
      return wishList;
   }

   public void clearWishList() {
      wishList.clear();
   }

   public void addToWishList(int sn) {
      wishList.add(sn);
   }

   public void gift(int recipient, String from, String message, int sn) {
      gift(recipient, from, message, sn, -1);
   }

   public void gift(int recipient, String from, String message, int sn, int ringid) {
      DatabaseConnection.getInstance().withConnection(connection -> GiftAdministrator.getInstance().createGift(connection, recipient, from, message, sn, ringid));
   }

   public List<Gift> loadGifts() {
      List<Gift> gifts = new ArrayList<>();
      DatabaseConnection.getInstance().withConnection(connection -> {
         GiftProvider.getInstance().getGiftsForCharacter(connection, characterId).forEach(gift -> loadGift(gifts, gift));
         GiftAdministrator.getInstance().deleteAllGiftsForCharacter(connection, characterId);
      });
      return gifts;
   }

   private void loadGift(List<Gift> gifts, GiftData gift) {
      notes++;
      CashItem cItem = CashItemFactory.getItem(gift.sn());
      Item item = cItem.toItem();
      Equip equip = null;
      item.giftFrom_$eq(gift.from());
      if (item.inventoryType().equals(MapleInventoryType.EQUIP)) {
         equip = (Equip) item;
         equip.ringId_$eq(gift.ringId());
         gifts.add(new Gift(equip, gift.message()));
      } else {
         gifts.add(new Gift(item, gift.message()));
      }

      if (CashItemFactory.isPackage(cItem.getItemId())) { //Packages never contains a ring
         for (Item packageItem : CashItemFactory.getPackage(cItem.getItemId())) {
            packageItem.giftFrom_$eq(gift.from());
            addToInventory(packageItem);
         }
      } else {
         addToInventory(equip == null ? item : equip);
      }
   }

   public int getAvailableNotes() {
      return notes;
   }

   public void decreaseNotes() {
      notes--;
   }

   public void save(EntityManager entityManager) {
      AccountAdministrator.getInstance().saveNxInformation(entityManager, accountId, nxCredit, maplePoint, nxPrepaid);

      List<Pair<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

      List<Item> inv = getInventory();
      for (Item item : inv) {
         itemsWithType.add(new Pair<>(item, item.inventoryType()));
      }

      factory.saveItems(itemsWithType, accountId, entityManager);

      WishListAdministrator.getInstance().deleteForCharacter(entityManager, characterId);
      WishListAdministrator.getInstance().addForCharacter(entityManager, characterId, wishList);
   }

   private Item getCashShopItemByItemid(int itemid) {
      lock.lock();
      try {
         for (Item it : inventory) {
            if (it.id() == itemid) {
               return it;
            }
         }
      } finally {
         lock.unlock();
      }

      return null;
   }

   public synchronized Pair<Item, Item> openCashShopSurprise() {
      Item css = getCashShopItemByItemid(5222000);

      if (css != null) {
         CashItem cItem = CashItemFactory.getRandomCashItem();

         if (cItem != null) {
            if (css.quantity() > 1) {
                    /* if(NOT ENOUGH SPACE) { looks like we're not dealing with cash inventory limit whatsoever, k then
                        return null;
                    } */

               css.quantity_$eq((short) (css.quantity() - 1));
            } else {
               removeFromInventory(css);
            }

            Item item = cItem.toItem();
            addToInventory(item);

            return new Pair<>(item, css);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class CashItem {

      private int sn, itemId, price;
      private long period;
      private short count;
      private boolean onSale;

      private CashItem(int sn, int itemId, int price, long period, short count, boolean onSale) {
         this.sn = sn;
         this.itemId = itemId;
         this.price = price;
         this.period = (period == 0 ? 90 : period);
         this.count = count;
         this.onSale = onSale;
      }

      public int getSN() {
         return sn;
      }

      public int getItemId() {
         return itemId;
      }

      public int getPrice() {
         return price;
      }

      public short getCount() {
         return count;
      }

      public boolean isOnSale() {
         return onSale;
      }

      public Item toItem() {
         Item item;

         int petid = -1;
         if (ItemConstants.isPet(itemId)) {
            petid = PetProcessor.getInstance().createPet(itemId);
         }

         if (ItemConstants.getInventoryType(itemId).equals(MapleInventoryType.EQUIP)) {
            item = MapleItemInformationProvider.getInstance().getEquipById(itemId);
         } else {
            item = BetterItemFactory.getInstance().create(itemId, (byte) 0, count, petid);
         }

         if (ItemConstants.EXPIRING_ITEMS) {
            if (period == 1) {
               if (itemId == 5211048 || itemId == 5360042) { // 4 Hour 2X coupons, the period is 1, but we don't want them to last a day.
                  item.expiration_(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 4));
                            /*
                            } else if(itemId == 5211047 || itemId == 5360014) { // 3 Hour 2X coupons, unused as of now
                                    item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 3));
                            */
               } else if (itemId == 5211060) { // 2 Hour 3X coupons.
                  item.expiration_(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 2));
               } else {
                  item.expiration_(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24));
               }
            } else {
               item.expiration_(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24 * period));
            }
         }

         item.sn_$eq(sn);
         return item;
      }
   }

   public static class SpecialCashItem {
      private int sn, modifier;
      private byte info; //?

      public SpecialCashItem(int sn, int modifier, byte info) {
         this.sn = sn;
         this.modifier = modifier;
         this.info = info;
      }

      public int getSN() {
         return sn;
      }

      public int getModifier() {
         return modifier;
      }

      public byte getInfo() {
         return info;
      }
   }

   public static class CashItemFactory {

      private static final Map<Integer, CashItem> items = new HashMap<>();
      private static final Map<Integer, List<Integer>> packages = new HashMap<>();
      private static final List<SpecialCashItem> specialcashitems = new ArrayList<>();
      private static final List<Integer> randomitemsns = new ArrayList<>();

      static {
         MapleDataProvider etc = MapleDataProviderFactory.getDataProvider(new File("wz/Etc.wz"));

         for (MapleData item : etc.getData("Commodity.img").getChildren()) {
            int sn = MapleDataTool.getIntConvert("SN", item);
            int itemId = MapleDataTool.getIntConvert("ItemId", item);
            int price = MapleDataTool.getIntConvert("Price", item, 0);
            long period = MapleDataTool.getIntConvert("Period", item, 1);
            short count = (short) MapleDataTool.getIntConvert("Count", item, 1);
            boolean onSale = MapleDataTool.getIntConvert("OnSale", item, 0) == 1;
            items.put(sn, new CashItem(sn, itemId, price, period, count, onSale));
         }

         for (MapleData cashPackage : etc.getData("CashPackage.img").getChildren()) {
            List<Integer> cPackage = new ArrayList<>();

            for (MapleData item : cashPackage.getChildByPath("SN").getChildren()) {
               cPackage.add(Integer.parseInt(item.getData().toString()));
            }

            packages.put(Integer.parseInt(cashPackage.getName()), cPackage);
         }

         for (Entry<Integer, CashItem> e : items.entrySet()) {
            if (e.getValue().isOnSale()) {
               randomitemsns.add(e.getKey());
            }
         }

         DatabaseConnection.getInstance().withConnectionResult(connection -> SpecialCashItemProvider.getInstance().getSpecialCashItems(connection)).ifPresent(specialcashitems::addAll);
      }

      public static CashItem getRandomCashItem() {
         if (randomitemsns.isEmpty()) {
            return null;
         }

         int rnd = (int) (Math.random() * randomitemsns.size());
         return items.get(randomitemsns.get(rnd));
      }

      public static CashItem getItem(int sn) {
         return items.get(sn);
      }

      public static List<Item> getPackage(int itemId) {
         List<Item> cashPackage = new ArrayList<>();

         for (int sn : packages.get(itemId)) {
            cashPackage.add(getItem(sn).toItem());
         }

         return cashPackage;
      }

      public static boolean isPackage(int itemId) {
         return packages.containsKey(itemId);
      }

      public static List<SpecialCashItem> getSpecialCashItems() {
         return specialcashitems;
      }

      public static void reloadSpecialCashItems() {//Yay?
         specialcashitems.clear();
         DatabaseConnection.getInstance().withConnectionResult(connection -> SpecialCashItemProvider.getInstance().getSpecialCashItems(connection)).ifPresent(specialcashitems::addAll);
      }
   }
}
