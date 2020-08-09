package client.database.utility;

import client.database.data.GetInventoryItems;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.processor.ItemProcessor;
import client.processor.PetProcessor;
import tools.Pair;
import transformer.SqlTransformer;

public class InventoryItemTransformer implements SqlTransformer<Pair<Item, MapleInventoryType>, GetInventoryItems> {
   @Override
   public Pair<Item, MapleInventoryType> transform(GetInventoryItems resultSet) {
      EquipFromResultSetTransformer equipTransformer = new EquipFromResultSetTransformer();

      MapleInventoryType inventoryType = MapleInventoryType.getByType((byte) resultSet.inventoryType());
      if (inventoryType != null) {
         if (inventoryType.equals(MapleInventoryType.EQUIP) || inventoryType.equals(MapleInventoryType.EQUIPPED)) {
            return new Pair<>(equipTransformer.transform(resultSet), inventoryType);
         } else {
            MaplePet pet = PetProcessor.getInstance().loadFromDb(resultSet.itemId(), (short) 0, resultSet.petId());
            Item item = Item.newBuilder(resultSet.itemId())
                  .setPosition((short) resultSet.position())
                  .setQuantity((short) resultSet.quantity())
                  .setPet(pet)
                  .setPetId(resultSet.petId())
                  .setOwner(resultSet.owner())
                  .setExpiration(resultSet.expiration())
                  .setGiftFrom(resultSet.giftFrom())
                  .setFlag(ItemProcessor.getInstance().setFlag(resultSet.itemId(), (short) resultSet.flag()))
                  .build();
            return new Pair<>(item, inventoryType);
         }
      }
      return null;
   }
}
