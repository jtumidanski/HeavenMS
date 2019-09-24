package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.inventory.Equip;
import client.processor.ItemProcessor;
import server.MapleItemInformationProvider;

public class EquipFromResultSetTransformer implements SqlTransformer<Equip, ResultSet> {
   @Override
   public Equip transform(ResultSet resultSet) throws SQLException {
      int id = resultSet.getInt("itemid");
      boolean isElemental = (MapleItemInformationProvider.getInstance().getEquipLevel(id, false) > 1);
      Equip equip = new Equip(id, (short) resultSet.getInt("position"), isElemental);
      equip.owner_$eq(resultSet.getString("owner"));
      equip.quantity_$eq((short) resultSet.getInt("quantity"));
      equip.acc_$eq((short) resultSet.getInt("acc"));
      equip.avoid_$eq((short) resultSet.getInt("avoid"));
      equip.dex_$eq((short) resultSet.getInt("dex"));
      equip.hands_$eq((short) resultSet.getInt("hands"));
      equip.hp_$eq((short) resultSet.getInt("hp"));
      equip._int_$eq((short) resultSet.getInt("int"));
      equip.jump_$eq((short) resultSet.getInt("jump"));
      equip.vicious_$eq((short) resultSet.getInt("vicious"));
      ItemProcessor.getInstance().setFlag(equip, (short) resultSet.getInt("flag"));
      equip.luk_$eq((short) resultSet.getInt("luk"));
      equip.matk_$eq((short) resultSet.getInt("matk"));
      equip.mdef_$eq((short) resultSet.getInt("mdef"));
      equip.mp_$eq((short) resultSet.getInt("mp"));
      equip.speed_$eq((short) resultSet.getInt("speed"));
      equip.str_$eq((short) resultSet.getInt("str"));
      equip.watk_$eq((short) resultSet.getInt("watk"));
      equip.wdef_$eq((short) resultSet.getInt("wdef"));
      equip.slots_$eq((byte) resultSet.getInt("upgradeslots"));
      equip.level_$eq(resultSet.getByte("level"));
      equip.itemExp_$eq(resultSet.getInt("itemexp"));
      equip.itemLevel_$eq(resultSet.getByte("itemlevel"));
      equip.expiration_(resultSet.getLong("expiration"));
      equip.giftFrom_$eq(resultSet.getString("giftFrom"));
      equip.ringId_$eq(resultSet.getInt("ringid"));
      return equip;
   }
}
