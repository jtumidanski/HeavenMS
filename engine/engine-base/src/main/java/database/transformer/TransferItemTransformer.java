package database.transformer;

import client.inventory.Equip;
import client.inventory.Item;
import client.processor.ItemProcessor;
import entity.mts.MtsItem;
import server.MapleItemInformationProvider;
import transformer.SqlTransformer;

public class TransferItemTransformer implements SqlTransformer<Item, MtsItem> {
   @Override
   public Item transform(MtsItem resultSet) {
      if (resultSet.getType() != 1) {
         return Item.newBuilder(resultSet.getItemId())
               .setPosition((short) 0)
               .setQuantity(resultSet.getQuantity().shortValue())
               .setOwner(resultSet.getOwner())
               .build();
      } else {
         int id = resultSet.getItemId();
         boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);
         return Equip.newBuilder(id)
               .setPosition(resultSet.getPosition().shortValue())
               .setSlots(-1)
               .setElemental(isElemental)
               .setOwner(resultSet.getOwner())
               .setQuantity((short) 1)
               .setAcc(resultSet.getAcc())
               .setAvoid(resultSet.getAvoid())
               .setDex(resultSet.getDex())
               .setHands(resultSet.getHands())
               .setHp(resultSet.getHp())
               .setIntelligence(resultSet.getIntelligence())
               .setJump(resultSet.getJump())
               .setLuk(resultSet.getLuk())
               .setMatk(resultSet.getMatk())
               .setMdef(resultSet.getMdef())
               .setMp(resultSet.getMp())
               .setSpeed(resultSet.getSpeed())
               .setStr(resultSet.getStr())
               .setWatk(resultSet.getWatk())
               .setWdef(resultSet.getWdef())
               .setSlots(resultSet.getUpgradeSlots())
               .setLevel(resultSet.getLevel().byteValue())
               .setVicious(resultSet.getVicious())
               .setFlag(ItemProcessor.getInstance().setFlag(id, resultSet.getFlag().shortValue()))
               .setItemLevel(resultSet.getItemLevel().byteValue())
               .setItemExp(resultSet.getItemExp())
               .setRingId(resultSet.getRingId())
               .setExpiration(resultSet.getExpiration())
               .setGiftFrom(resultSet.getGiftFrom())
               .build();
      }
   }
}
