package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFactory;
import client.inventory.MapleInventory;
import client.inventory.manipulator.MapleInventoryManipulator;
import constants.MapleInventoryType;
import database.DatabaseConnection;
import scripting.event.EventInstanceManager;
import scripting.event.EventManager;
import tools.Pair;

public class MapleMarriage extends EventInstanceManager {

   public MapleMarriage(EventManager em, String name) {
      super(em, name);
   }

   public static boolean claimGiftItems(MapleClient c, MapleCharacter chr) {
      List<Item> gifts = loadGiftItemsFromDb(c, chr.getId());
      if (MapleInventory.checkSpot(chr, gifts)) {
         // TODO - Is this new LinkedList() a bug?
         DatabaseConnection.getInstance().withConnection(connection -> ItemFactory.MARRIAGE_GIFTS.saveItems(new LinkedList<>(), chr.getId(), connection));
         for (Item item : gifts) {
            MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
         }

         return true;
      }

      return false;
   }

   public static List<Item> loadGiftItemsFromDb(MapleClient c, int cid) {
      List<Item> items = new LinkedList<>();
      for (Pair<Item, MapleInventoryType> it : ItemFactory.MARRIAGE_GIFTS.loadItems(cid, false)) {
         items.add(it.getLeft());
      }
      return items;
   }

   public static void saveGiftItemsToDb(MapleClient c, List<Item> giftItems, int cid) {
      List<Pair<Item, MapleInventoryType>> items = new LinkedList<>();
      for (Item it : giftItems) {
         items.add(new Pair<>(it, it.inventoryType()));
      }
      DatabaseConnection.getInstance().withConnection(entityManager -> ItemFactory.MARRIAGE_GIFTS.saveItems(items, cid, entityManager));
   }

   public boolean giftItemToSpouse(int cid) {
      return this.getIntProperty("wishlistSelection") == 0;
   }

   public List<String> getWishListItems(boolean groom) {
      String strItems = this.getProperty(groom ? "groomWishlist" : "brideWishlist");
      if (strItems != null) {
         return Arrays.asList(strItems.split("\r\n"));
      }

      return new LinkedList<>();
   }

   public void initializeGiftItems() {
      List<Item> groomGifts = new ArrayList<>();
      this.setObjectProperty("groomGiftlist", groomGifts);

      List<Item> brideGifts = new ArrayList<>();
      this.setObjectProperty("brideGiftlist", brideGifts);
   }

   public List<Item> getGiftItems(MapleClient c, boolean groom) {
      List<Item> gifts = getGiftItemsList(groom);
      synchronized (gifts) {
         return new LinkedList<>(gifts);
      }
   }

   private List<Item> getGiftItemsList(boolean groom) {
      return (List<Item>) this.getObjectProperty(groom ? "groomGiftlist" : "brideGiftlist");
   }

   public Item getGiftItem(MapleClient c, boolean groom, int idx) {
      try {
         return getGiftItems(c, groom).get(idx);
      } catch (IndexOutOfBoundsException e) {
         return null;
      }
   }

   public void addGiftItem(boolean groom, Item item) {
      List<Item> gifts = getGiftItemsList(groom);
      synchronized (gifts) {
         gifts.add(item);
      }
   }

   public void removeGiftItem(boolean groom, Item item) {
      List<Item> gifts = getGiftItemsList(groom);
      synchronized (gifts) {
         gifts.remove(item);
      }
   }

   public Boolean isMarriageGroom(MapleCharacter chr) {
      Boolean groom = null;
      try {
         int groomId = this.getIntProperty("groomId"), brideId = this.getIntProperty("brideId");
         if (chr.getId() == groomId) {
            groom = true;
         } else if (chr.getId() == brideId) {
            groom = false;
         }
      } catch (NumberFormatException ignored) {
      }

      return groom;
   }

   public void saveGiftItemsToDb(MapleClient c, boolean groom, int cid) {
      MapleMarriage.saveGiftItemsToDb(c, getGiftItems(c, groom), cid);
   }
}
