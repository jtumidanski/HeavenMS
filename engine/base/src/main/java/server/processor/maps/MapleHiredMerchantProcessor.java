package server.processor.maps;

import java.util.List;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import server.maps.MaplePlayerShopItem;
import tools.Pair;

public class MapleHiredMerchantProcessor {
   private static MapleHiredMerchantProcessor ourInstance = new MapleHiredMerchantProcessor();

   public static MapleHiredMerchantProcessor getInstance() {
      return ourInstance;
   }

   private MapleHiredMerchantProcessor() {
   }

   public boolean canBuy(MapleClient c, Item newItem) {    // thanks xiaokelvin (Conrad) for noticing a leaked test code here
      return MapleInventoryManipulator.checkSpace(c, newItem.id(), newItem.quantity(), newItem.owner()) && MapleInventoryManipulator.addFromDrop(c, newItem, false);
   }

   public boolean check(MapleCharacter chr, List<MaplePlayerShopItem> items) {
      List<Pair<Item, MapleInventoryType>> result = items.stream().map(this::getItemMapleInventoryTypePair).collect(Collectors.toList());
      return MapleInventory.checkSpotsAndOwnership(chr, result);
   }

   private Pair<Item, MapleInventoryType> getItemMapleInventoryTypePair(MaplePlayerShopItem item) {
      Item it = item.getItem().copy();
      it.quantity_$eq((short) (it.quantity() * item.getBundles()));
      return new Pair<>(it, it.inventoryType());
   }
}
