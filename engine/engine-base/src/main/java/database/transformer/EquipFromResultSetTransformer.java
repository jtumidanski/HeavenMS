package database.transformer;

import client.database.data.GetInventoryItems;
import client.inventory.Equip;
import client.processor.ItemProcessor;
import server.MapleItemInformationProvider;
import transformer.SqlTransformer;

public class EquipFromResultSetTransformer implements SqlTransformer<Equip, GetInventoryItems> {
   @Override
   public Equip transform(GetInventoryItems resultSet) {
      int id = resultSet.itemId();
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);

      return Equip.newBuilder(id)
            .setPosition(resultSet.position().shortValue())
            .setElemental(isElemental)
            .setOwner(resultSet.owner())
            .setQuantity(resultSet.quantity().shortValue())
            .setAcc(resultSet.acc().shortValue())
            .setAvoid(resultSet.avoid().shortValue())
            .setDex(resultSet.dex().shortValue())
            .setHands(resultSet.hands().shortValue())
            .setHp(resultSet.hp().shortValue())
            .setIntelligence(resultSet.intelligence().shortValue())
            .setJump(resultSet.jump().shortValue())
            .setVicious(resultSet.vicious().shortValue())
            .setFlag(ItemProcessor.getInstance().setFlag(id, resultSet.flag().shortValue()))
            .setLuk(resultSet.luk().shortValue())
            .setMatk(resultSet.matk().shortValue())
            .setMdef(resultSet.mdef().shortValue())
            .setMp(resultSet.mp().shortValue())
            .setSpeed(resultSet.speed().shortValue())
            .setStr(resultSet.str().shortValue())
            .setWatk(resultSet.watk().shortValue())
            .setWdef(resultSet.wdef().shortValue())
            .setSlots(resultSet.upgradeSlots().byteValue())
            .setLevel(resultSet.level().byteValue())
            .setItemExp(resultSet.itemExp())
            .setItemLevel(resultSet.itemLevel().byteValue())
            .setExpiration(resultSet.expiration())
            .setGiftFrom(resultSet.giftFrom())
            .setRingId(resultSet.ringId())
            .build();
   }
}
