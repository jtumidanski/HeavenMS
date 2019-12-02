package server.processor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import client.MapleClient;
import database.provider.ShopItemProvider;
import database.provider.ShopProvider;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import constants.inventory.ItemConstants;
import constants.ShopTransactionOperation;
import scala.Option;
import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleShopItem;
import database.DatabaseConnection;
import tools.PacketCreator;
import tools.packet.shop.ConfirmShopTransaction;
import tools.packet.shop.GetNPCShop;

public class MapleShopProcessor {
   private static MapleShopProcessor instance;

   private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();

   static {
      for (int i = 2070000; i < 2070017; i++) {
         rechargeableItems.add(i);
      }
      rechargeableItems.add(2331000);//Blaze Capsule
      rechargeableItems.add(2332000);//Glaze Capsule
      rechargeableItems.add(2070018);
      rechargeableItems.remove(2070014); // doesn't exist
      for (int i = 2330000; i <= 2330005; i++) {
         rechargeableItems.add(i);
      }
   }

   public static MapleShopProcessor getInstance() {
      if (instance == null) {
         instance = new MapleShopProcessor();
      }
      return instance;
   }

   private MapleShopProcessor() {
   }

   private boolean canSell(Item item, short quantity) {
      if (item == null) { //Basic check
         return false;
      }

      short itemQuantity = item.quantity();
      if (itemQuantity == 0xFFFF) {
         itemQuantity = 1;
      } else if (itemQuantity < 0) {
         return false;
      }

      if (!ItemConstants.isRechargeable(item.id())) {
         return itemQuantity != 0 && quantity <= itemQuantity;
      }

      return true;
   }

   private short getSellingQuantity(Item item, short quantity) {
      if (ItemConstants.isRechargeable(item.id())) {
         quantity = item.quantity();
         if (quantity == 0xFFFF) {
            quantity = 1;
         }
      }

      return quantity;
   }

   public MapleShop createFromDB(int id, boolean isShopId) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> {
         Optional<MapleShop> ret;
         if (isShopId) {
            ret = ShopProvider.getInstance().getById(connection, id);
         } else {
            ret = ShopProvider.getInstance().getByNPC(connection, id);
         }
         if (ret.isPresent()) {
            MapleShop shop = ret.get();
            MapleShopItem[] result = ShopItemProvider.getInstance().getItemsForShop(connection, shop.id(), rechargeableItems).toArray(MapleShopItem[]::new);
            shop.setItems(result);
         }
         return ret.orElse(null);
      }).orElse(null);
   }

   public void sendShop(MapleShop shop, MapleClient c) {
      c.getPlayer().setShop(shop);
      PacketCreator.announce(c, new GetNPCShop(c, shop.npcId(), Arrays.asList(shop.items())));
   }

   public void buy(MapleShop shop, MapleClient c, short slot, int itemId, short quantity) {
      Option<MapleShopItem> itemResult = shop.findBySlot(slot);
      if (itemResult.isDefined()) {
         if (itemResult.get().itemId() != itemId) {
            System.out.println("Wrong slot number in shop " + shop.id());
            return;
         }
      } else {
         return;
      }

      MapleShopItem item = itemResult.get();
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (item.price() > 0) {
         int amount = (int) Math.min((float) item.price() * quantity, Integer.MAX_VALUE);
         if (c.getPlayer().getMeso() >= amount) {
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
               if (!ItemConstants.isRechargeable(itemId)) { //Pets can't be bought from shops
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", -1);
                  c.getPlayer().gainMeso(-amount, false);
               } else {
                  quantity = ii.getSlotMax(c, item.itemId());
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", -1);
                  c.getPlayer().gainMeso(-item.price(), false);
               }
               PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.DEFAULT));
            } else {
               PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.INVENTORY_FULL));
            }

         } else {
            PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.NOT_ENOUGH_MESO));
         }

      } else if (item.pitch() > 0) {
         int amount = (int) Math.min((float) item.pitch() * quantity, Integer.MAX_VALUE);

         if (c.getPlayer().getInventory(MapleInventoryType.ETC).countById(4310000) >= amount) {
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
               if (!ItemConstants.isRechargeable(itemId)) {
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", -1);
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4310000, amount, false, false);
               } else {
                  quantity = ii.getSlotMax(c, item.itemId());
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", -1);
                  MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4310000, amount, false, false);
               }
               PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.DEFAULT));
            } else {
               PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.INVENTORY_FULL));
            }
         }

      } else if (c.getPlayer().getInventory(MapleInventoryType.CASH).countById(shop.token()) != 0) {
         int amount = c.getPlayer().getInventory(MapleInventoryType.CASH).countById(shop.token());
         int value = amount * shop.tokenValue();
         int cost = item.price() * quantity;
         if (c.getPlayer().getMeso() + value >= cost) {
            int cardreduce = value - cost;
            int diff = cardreduce + c.getPlayer().getMeso();
            if (MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
               if (ItemConstants.isPet(itemId)) {
                  int petid = PetProcessor.getInstance().createPet(itemId);
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", petid, -1);
               } else {
                  MapleInventoryManipulator.addById(c, itemId, quantity, "", -1, -1);
               }
               c.getPlayer().gainMeso(diff, false);
            } else {
               PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.INVENTORY_FULL));
            }
            PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.DEFAULT));
         } else {
            PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.NOT_ENOUGH_MESO));
         }
      }
   }

   public void sell(MapleClient c, MapleInventoryType type, short slot, short quantity) {
      if (quantity == 0xFFFF || quantity == 0) {
         quantity = 1;
      } else if (quantity < 0) {
         return;
      }

      Item item = c.getPlayer().getInventory(type).getItem(slot);
      if (canSell(item, quantity)) {
         quantity = getSellingQuantity(item, quantity);
         MapleInventoryManipulator.removeFromSlot(c, type, (byte) slot, quantity, false);

         MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
         int recvMesos = ii.getPrice(item.id(), quantity);
         if (recvMesos > 0) {
            c.getPlayer().gainMeso(recvMesos, false);
         }
         PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.DEFAULT_2));
      } else {
         PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.NOT_ENOUGH_IN_STOCK_2));
      }
   }

   public void recharge(MapleClient c, short slot) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
      if (item == null || !ItemConstants.isRechargeable(item.id())) {
         return;
      }
      short slotMax = ii.getSlotMax(c, item.id());
      if (item.quantity() < 0) {
         return;
      }
      if (item.quantity() < slotMax) {
         int price = (int) Math.ceil(ii.getUnitPrice(item.id()) * (slotMax - item.quantity()));
         if (c.getPlayer().getMeso() >= price) {
            item.quantity_$eq(slotMax);
            c.getPlayer().forceUpdateItem(item);
            c.getPlayer().gainMeso(-price, false, true, false);
            PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.DEFAULT_2));
         } else {
            PacketCreator.announce(c, new ConfirmShopTransaction(ShopTransactionOperation.NOT_ENOUGH_MESO));
         }
      }
   }
}