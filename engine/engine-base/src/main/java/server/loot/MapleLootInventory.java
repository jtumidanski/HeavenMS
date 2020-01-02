package server.loot;

import java.util.HashMap;
import java.util.Map;

import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;

public class MapleLootInventory {
   Map<Integer, Integer> items = new HashMap<>(50);

   public MapleLootInventory(MapleCharacter from) {
      for (MapleInventoryType values : MapleInventoryType.values()) {

         for (Item it : from.getInventory(values).list()) {
            Integer itemQty = items.get(it.id());

            if (itemQty == null) {
               items.put(it.id(), (int) it.quantity());
            } else {
               items.put(it.id(), itemQty + it.quantity());
            }
         }
      }
   }

   public int hasItem(int itemId, int quantity) {
      Integer itemQty = items.get(itemId);
      return itemQty == null ? 0 : itemQty >= quantity ? 2 : itemQty > 0 ? 1 : 0;
   }

}
