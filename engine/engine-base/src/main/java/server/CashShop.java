package server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.processor.PetProcessor;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import database.DatabaseConnection;
import database.provider.SpecialCashItemProvider;
import net.server.Server;
import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.factory.MonitoredReentrantLockFactory;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;

public class CashShop {
   private int accountId, characterId, nxCredit, maplePoint, nxPrepaid, jobType;
   private boolean opened;
   private List<Item> inventory = new ArrayList<>();
   private List<Integer> wishList = new ArrayList<>();
   private int notes = 0;
   private Lock lock = MonitoredReentrantLockFactory.createLock(MonitoredLockType.CASH_SHOP);

   public CashShop(int accountId, int characterId, int jobType) {
      this.accountId = accountId;
      this.characterId = characterId;
      this.jobType = jobType;
   }

   public int getCash(int type) {
      return switch (type) {
         case 1 -> nxCredit;
         case 2 -> maplePoint;
         case 4 -> nxPrepaid;
         default -> 0;
      };

   }

   public void gainCash(int type, int cash) {
      switch (type) {
         case 1 -> nxCredit += cash;
         case 2 -> maplePoint += cash;
         case 4 -> nxPrepaid += cash;
      }
   }

   public void gainCash(int type, CashItem buyItem, int world) {
      gainCash(type, -buyItem.getPrice());
      if (!YamlConfig.config.server.USE_ENFORCE_ITEM_SUGGESTION) {
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

   public void updateInventory(Item item) {
      lock.lock();
      try {
         inventory = Stream.concat(inventory.stream().filter(i -> i.id() != item.id()), Stream.of(item))
               .collect(Collectors.toList());
      } finally {
         lock.unlock();
      }
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

   public int getAvailableNotes() {
      return notes;
   }

   public void addNote() {
      notes++;
   }

   public void decreaseNotes() {
      notes--;
   }

   private Item getCashShopItemByItemId(int itemId) {
      lock.lock();
      try {
         for (Item it : inventory) {
            if (it.id() == itemId) {
               return it;
            }
         }
      } finally {
         lock.unlock();
      }

      return null;
   }

   public synchronized Pair<Item, Item> openCashShopSurprise() {
      Item css = getCashShopItemByItemId(5222000);

      if (css != null) {
         CashItem cItem = CashItemFactory.getRandomCashItem();

         if (cItem != null) {
            if (css.quantity() > 1) {
                    /* if(NOT ENOUGH SPACE) { looks like we're not dealing with cash inventory limit whatsoever, k then
                        return null;
                    } */
               css = css.setQuantity((short) (css.quantity() - 1));
               updateInventory(css);
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

      public CashItem(int sn, int itemId, int price, long period, short count, boolean onSale) {
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

         int petId = -1;
         if (ItemConstants.isPet(itemId)) {
            petId = PetProcessor.getInstance().createPet(itemId);
         }

         if (ItemConstants.getInventoryType(itemId).equals(MapleInventoryType.EQUIP)) {
            item = MapleItemInformationProvider.getInstance().getEquipById(itemId);
         } else {
            MaplePet pet = PetProcessor.getInstance().loadFromDb(itemId, (short) 0, petId);
            item = Item.newBuilder(itemId)
                  .setPosition((short) 0)
                  .setQuantity(count)
                  .setPet(pet)
                  .setPetId(petId)
                  .build();
         }

         if (ItemConstants.EXPIRING_ITEMS) {
            if (period == 1) {
               if (itemId == 5211048 || itemId == 5360042) { // 4 Hour 2X coupons, the period is 1, but we don't want them to last a day.
                  item = item.expiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 4));
                            /*
                            } else if(itemId == 5211047 || itemId == 5360014) { // 3 Hour 2X coupons, unused as of now
                                    item.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 3));
                            */
               } else if (itemId == 5211060) { // 2 Hour 3X coupons.
                  item = item.expiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 2));
               } else {
                  item = item.expiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24));
               }
            } else {
               item = item.expiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24 * period));
            }
         }

         item = item.setSn(sn);
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

      private static final Map<Integer, CashItem> ITEMS = new HashMap<>();
      private static final Map<Integer, List<Integer>> PACKAGES = new HashMap<>();
      private static final List<SpecialCashItem> SPECIAL_CASH_ITEMS = new ArrayList<>();
      private static final List<Integer> RANDOM_ITEM_SNS = new ArrayList<>();

      static {
         MapleDataProvider etc = MapleDataProviderFactory.getDataProvider(new File("wz/Etc.wz"));

         for (MapleData item : etc.getData("Commodity.img").getChildren()) {
            int sn = MapleDataTool.getIntConvert("SN", item);
            int itemId = MapleDataTool.getIntConvert("ItemId", item);
            int price = MapleDataTool.getIntConvert("Price", item, 0);
            long period = MapleDataTool.getIntConvert("Period", item, 1);
            short count = (short) MapleDataTool.getIntConvert("Count", item, 1);
            boolean onSale = MapleDataTool.getIntConvert("OnSale", item, 0) == 1;
            ITEMS.put(sn, new CashItem(sn, itemId, price, period, count, onSale));
         }

         for (MapleData cashPackage : etc.getData("CashPackage.img").getChildren()) {
            List<Integer> cPackage = new ArrayList<>();

            for (MapleData item : cashPackage.getChildByPath("SN").getChildren()) {
               cPackage.add(Integer.parseInt(item.getData().toString()));
            }

            PACKAGES.put(Integer.parseInt(cashPackage.getName()), cPackage);
         }

         for (Entry<Integer, CashItem> e : ITEMS.entrySet()) {
            if (e.getValue().isOnSale()) {
               RANDOM_ITEM_SNS.add(e.getKey());
            }
         }

         DatabaseConnection.getInstance().withConnectionResult(connection -> SpecialCashItemProvider.getInstance().getSpecialCashItems(connection)).ifPresent(SPECIAL_CASH_ITEMS::addAll);
      }

      public static CashItem getRandomCashItem() {
         if (RANDOM_ITEM_SNS.isEmpty()) {
            return null;
         }

         int rnd = (int) (Math.random() * RANDOM_ITEM_SNS.size());
         return ITEMS.get(RANDOM_ITEM_SNS.get(rnd));
      }

      public static CashItem getItem(int sn) {
         return ITEMS.get(sn);
      }

      public static List<Item> getPackage(int itemId) {
         List<Item> cashPackage = new ArrayList<>();

         for (int sn : PACKAGES.get(itemId)) {
            cashPackage.add(getItem(sn).toItem());
         }

         return cashPackage;
      }

      public static boolean isPackage(int itemId) {
         return PACKAGES.containsKey(itemId);
      }

      public static List<SpecialCashItem> getSpecialCashItems() {
         return SPECIAL_CASH_ITEMS;
      }

      public static void reloadSpecialCashItems() {//Yay?
         SPECIAL_CASH_ITEMS.clear();
         DatabaseConnection.getInstance().withConnectionResult(connection -> SpecialCashItemProvider.getInstance().getSpecialCashItems(connection)).ifPresent(SPECIAL_CASH_ITEMS::addAll);
      }
   }

   public int getAccountId() {
      return accountId;
   }

   public int getCharacterId() {
      return characterId;
   }

   public int getNxCredit() {
      return nxCredit;
   }

   public int getMaplePoint() {
      return maplePoint;
   }

   public int getNxPrepaid() {
      return nxPrepaid;
   }

   public int getJobType() {
      return jobType;
   }
}
