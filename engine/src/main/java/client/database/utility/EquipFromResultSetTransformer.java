package client.database.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import client.inventory.Equip;

public class EquipFromResultSetTransformer implements SqlTransformer<Equip, ResultSet> {
   @Override
   public Equip transform(ResultSet resultSet) throws SQLException {
      Equip equip = new Equip(resultSet.getInt("itemid"), (short) resultSet.getInt("position"));
      equip.setOwner(resultSet.getString("owner"));
      equip.setQuantity((short) resultSet.getInt("quantity"));
      equip.setAcc((short) resultSet.getInt("acc"));
      equip.setAvoid((short) resultSet.getInt("avoid"));
      equip.setDex((short) resultSet.getInt("dex"));
      equip.setHands((short) resultSet.getInt("hands"));
      equip.setHp((short) resultSet.getInt("hp"));
      equip.setInt((short) resultSet.getInt("int"));
      equip.setJump((short) resultSet.getInt("jump"));
      equip.setVicious((short) resultSet.getInt("vicious"));
      equip.setFlag((short) resultSet.getInt("flag"));
      equip.setLuk((short) resultSet.getInt("luk"));
      equip.setMatk((short) resultSet.getInt("matk"));
      equip.setMdef((short) resultSet.getInt("mdef"));
      equip.setMp((short) resultSet.getInt("mp"));
      equip.setSpeed((short) resultSet.getInt("speed"));
      equip.setStr((short) resultSet.getInt("str"));
      equip.setWatk((short) resultSet.getInt("watk"));
      equip.setWdef((short) resultSet.getInt("wdef"));
      equip.setUpgradeSlots((byte) resultSet.getInt("upgradeslots"));
      equip.setLevel(resultSet.getByte("level"));
      equip.setItemExp(resultSet.getInt("itemexp"));
      equip.setItemLevel(resultSet.getByte("itemlevel"));
      equip.setExpiration(resultSet.getLong("expiration"));
      equip.setGiftFrom(resultSet.getString("giftFrom"));
      equip.setRingId(resultSet.getInt("ringid"));
      return equip;
   }
}
