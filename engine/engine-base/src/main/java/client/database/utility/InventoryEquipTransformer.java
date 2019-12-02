package client.database.utility;

import client.database.data.GetInventoryItems;
import client.inventory.Item;
import database.SqlTransformer;
import tools.Pair;

public class InventoryEquipTransformer implements SqlTransformer<Pair<Item, Integer>, GetInventoryItems> {
   @Override
   public Pair<Item, Integer> transform(GetInventoryItems getInventoryItems) {
      EquipFromResultSetTransformer equipTransformer = new EquipFromResultSetTransformer();
      return new Pair<>(equipTransformer.transform(getInventoryItems), getInventoryItems.characterId());
   }
}
