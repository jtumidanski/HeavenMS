package client.database.administrator;

import java.sql.Connection;
import java.util.Arrays;

import client.database.AbstractQueryExecutor;

public class InventoryEquipmentAdministrator extends AbstractQueryExecutor {
   private static InventoryEquipmentAdministrator instance;

   public static InventoryEquipmentAdministrator getInstance() {
      if (instance == null) {
         instance = new InventoryEquipmentAdministrator();
      }
      return instance;
   }

   private InventoryEquipmentAdministrator() {
   }

   public void create(Connection connection, int inventoryItemId, int upgradeSlots, int level, int strength,
                      int dexterity, int intelligence, int luck, int hp, int mp, int weaponAttack, int magicAttack,
                      int weaponDefense, int magicDefense, int accuracy, int avoidability, int hands, int speed,
                      int jump, int locked, int vicious, int itemLevel, int itemExp, int ringId) {
      String sql = "INSERT INTO `inventoryequipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, inventoryItemId);
         ps.setInt(2, upgradeSlots);
         ps.setInt(3, level);
         ps.setInt(4, strength);
         ps.setInt(5, dexterity);
         ps.setInt(6, intelligence);
         ps.setInt(7, luck);
         ps.setInt(8, hp);
         ps.setInt(9, mp);
         ps.setInt(10, weaponAttack);
         ps.setInt(11, magicAttack);
         ps.setInt(12, weaponDefense);
         ps.setInt(13, magicDefense);
         ps.setInt(14, accuracy);
         ps.setInt(15, avoidability);
         ps.setInt(16, hands);
         ps.setInt(17, speed);
         ps.setInt(18, jump);
         ps.setInt(19, locked);
         ps.setInt(20, vicious);
         ps.setInt(21, itemLevel);
         ps.setInt(22, itemExp);
         ps.setInt(23, ringId);
      });
   }

   public void updateRing(Connection connection, int ringId, int partnerRingId) {
      String sql = "UPDATE inventoryequipment SET ringid=-1 WHERE ringid=?";
      batch(connection, sql, (ps, data) -> ps.setInt(1, data), Arrays.asList(ringId, partnerRingId));
   }
}