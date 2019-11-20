package client.database.utility;

import client.database.data.GetInventoryItems;
import client.inventory.Equip;
import client.processor.ItemProcessor;
import server.MapleItemInformationProvider;

public class EquipFromResultSetTransformer implements SqlTransformer<Equip, GetInventoryItems> {
   @Override
   public Equip transform(GetInventoryItems resultSet) {
      int id = resultSet.itemId();
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);
      Equip equip = new Equip(id, (short) resultSet.position(), isElemental);
      equip.owner_$eq(resultSet.owner());
      equip.quantity_$eq((short) resultSet.quantity());
      equip.acc_$eq((short) resultSet.acc());
      equip.avoid_$eq((short) resultSet.avoid());
      equip.dex_$eq((short) resultSet.dex());
      equip.hands_$eq((short) resultSet.hands());
      equip.hp_$eq((short) resultSet.hp());
      equip._int_$eq((short) resultSet.intelligence());
      equip.jump_$eq((short) resultSet.jump());
      equip.vicious_$eq((short) resultSet.vicious());
      ItemProcessor.getInstance().setFlag(equip, (short) resultSet.flag());
      equip.luk_$eq((short) resultSet.luk());
      equip.matk_$eq((short) resultSet.matk());
      equip.mdef_$eq((short) resultSet.mdef());
      equip.mp_$eq((short) resultSet.mp());
      equip.speed_$eq((short) resultSet.speed());
      equip.str_$eq((short) resultSet.str());
      equip.watk_$eq((short) resultSet.watk());
      equip.wdef_$eq((short) resultSet.wdef());
      equip.slots_$eq((byte) resultSet.upgradeSlots());
      equip.level_$eq((byte) resultSet.level());
      equip.itemExp_$eq(resultSet.itemExp());
      equip.itemLevel_$eq((byte) resultSet.itemLevel());
      equip.expiration_(resultSet.expiration());
      equip.giftFrom_$eq(resultSet.giftFrom());
      equip.ringId_$eq(resultSet.ringId());
      return equip;
   }
}
