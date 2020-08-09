package client.database.utility;

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
            .setPosition((short) resultSet.position())
            .setElemental(isElemental)
            .setOwner(resultSet.owner())
            .setQuantity((short) resultSet.quantity())
            .setAcc((short) resultSet.acc())
            .setAvoid((short) resultSet.avoid())
            .setDex((short) resultSet.dex())
            .setHands((short) resultSet.hands())
            .setHp((short) resultSet.hp())
            .setIntelligence((short) resultSet.intelligence())
            .setJump((short) resultSet.jump())
            .setVicious((short) resultSet.vicious())
            .setFlag(ItemProcessor.getInstance().setFlag(id, (short) resultSet.flag()))
            .setLuk((short) resultSet.luk())
            .setMatk((short) resultSet.matk())
            .setMdef((short) resultSet.mdef())
            .setMp((short) resultSet.mp())
            .setSpeed((short) resultSet.speed())
            .setStr((short) resultSet.str())
            .setWatk((short) resultSet.watk())
            .setWdef((short) resultSet.wdef())
            .setSlots((byte) resultSet.upgradeSlots())
            .setLevel((byte) resultSet.level())
            .setItemExp(resultSet.itemExp())
            .setItemLevel((byte) resultSet.itemLevel())
            .setExpiration(resultSet.expiration())
            .setGiftFrom(resultSet.giftFrom())
            .setRingId(resultSet.ringId())
            .build();
   }
}
