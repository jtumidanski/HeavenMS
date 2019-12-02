package client.database.utility;

import client.database.data.GetInventoryItems;
import client.inventory.BetterItemFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.processor.ItemProcessor;
import database.SqlTransformer;
import tools.Pair;

public class InventoryItemTransformer implements SqlTransformer<Pair<Item, MapleInventoryType>, GetInventoryItems> {
   @Override
   public Pair<Item, MapleInventoryType> transform(GetInventoryItems resultSet) {
      EquipFromResultSetTransformer equipTransformer = new EquipFromResultSetTransformer();

      MapleInventoryType inventoryType = MapleInventoryType.getByType((byte) resultSet.inventoryType());
      if (inventoryType != null) {
         if (inventoryType.equals(MapleInventoryType.EQUIP) || inventoryType.equals(MapleInventoryType.EQUIPPED)) {
            return new Pair<>(equipTransformer.transform(resultSet), inventoryType);
         } else {
            Item item = BetterItemFactory.getInstance().create(resultSet.itemId(), (byte) resultSet.position(),
                  (short) resultSet.quantity(), resultSet.petId());
            item.owner_$eq(resultSet.owner());
            item.expiration_(resultSet.expiration());
            item.giftFrom_$eq(resultSet.giftFrom());
            ItemProcessor.getInstance().setFlag(item, (short) resultSet.flag());
            return new Pair<>(item, inventoryType);
         }
      }
      return null;
   }
}
