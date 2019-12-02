package client.database.utility;

import client.inventory.Equip;
import client.inventory.Item;
import client.processor.ItemProcessor;
import database.SqlTransformer;
import entity.mts.MtsItem;
import server.MTSItemInfo;
import server.MapleItemInformationProvider;

public class MtsItemInfoTransformer implements SqlTransformer<MTSItemInfo, MtsItem> {
   @Override
   public MTSItemInfo transform(MtsItem resultSet) {
      if (resultSet.getType() != 1) {
         Item i = new Item(resultSet.getItemId(), (short) 0, resultSet.getQuantity().shortValue());
         i.owner_$eq(resultSet.getOwner());
         return new MTSItemInfo(i, resultSet.getPrice(), resultSet.getId(), resultSet.getSeller(), resultSet.getSellerName(), resultSet.getSellEnds());
      } else {
         int id = resultSet.getItemId();
         boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);
         Equip equip = new Equip(id, resultSet.getPosition().byteValue(), -1, isElemental);
         equip.owner_$eq(resultSet.getOwner());
         equip.quantity_$eq((short) 1);
         equip.acc_$eq(resultSet.getAcc());
         equip.avoid_$eq(resultSet.getAvoid());
         equip.dex_$eq(resultSet.getDex());
         equip.hands_$eq(resultSet.getHands());
         equip.hp_$eq(resultSet.getHp());
         equip._int_$eq(resultSet.getIntelligence());
         equip.jump_$eq(resultSet.getJump());
         equip.vicious_$eq(resultSet.getVicious());
         equip.luk_$eq(resultSet.getLuk());
         equip.matk_$eq(resultSet.getMatk());
         equip.mdef_$eq(resultSet.getMdef());
         equip.mp_$eq(resultSet.getMp());
         equip.speed_$eq(resultSet.getSpeed());
         equip.str_$eq(resultSet.getStr());
         equip.watk_$eq(resultSet.getWatk());
         equip.wdef_$eq(resultSet.getWdef());
         equip.slots_$eq(resultSet.getUpgradeSlots());
         equip.level_$eq(resultSet.getLevel().byteValue());
         equip.itemLevel_$eq(resultSet.getItemLevel().byteValue());
         equip.itemExp_$eq(resultSet.getItemExp());
         equip.ringId_$eq(resultSet.getRingId());
         equip.expiration_(resultSet.getExpiration());
         equip.giftFrom_$eq(resultSet.getGiftFrom());
         ItemProcessor.getInstance().setFlag(equip, resultSet.getFlag().shortValue());
         return new MTSItemInfo(equip, resultSet.getPrice(), resultSet.getId(), resultSet.getSeller(), resultSet.getSellerName(), resultSet.getSellEnds());
      }
   }
}
