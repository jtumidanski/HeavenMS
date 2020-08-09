package server.processor.maps;

import java.util.List;
import java.util.Optional;
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

   public boolean canBuy(MapleClient c, Item newItem) {
      boolean hasSpace = MapleInventoryManipulator.checkSpace(c, newItem.id(), newItem.quantity(), newItem.owner());
      Optional<Item> result = MapleInventoryManipulator.addFromDrop(c, newItem, false);
      return hasSpace && result.isPresent();
   }

   public boolean check(MapleCharacter chr, List<MaplePlayerShopItem> items) {
      List<Pair<Item, MapleInventoryType>> result = items.stream().map(this::getItemMapleInventoryTypePair).collect(Collectors.toList());
      return MapleInventory.checkSpotsAndOwnership(chr, result);
   }

   private Pair<Item, MapleInventoryType> getItemMapleInventoryTypePair(MaplePlayerShopItem item) {
      Item it = item.item().copy();
      it = it.setQuantity((short) (it.quantity() * item.bundles()));
      return new Pair<>(it, it.inventoryType());
   }
}
