package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import client.inventory.manipulator.MapleCashIdGenerator;
import server.MapleItemInformationProvider;

public class PetAdministrator extends AbstractQueryExecutor {
   private static PetAdministrator instance;

   public static PetAdministrator getInstance() {
      if (instance == null) {
         instance = new PetAdministrator();
      }
      return instance;
   }

   private PetAdministrator() {
   }

   public void unreferenceMissingPetsFromInventory(Connection connection) {
      String sql = "UPDATE inventoryitems SET petid = -1, expiration = 0 WHERE petid != -1 AND petid NOT IN (SELECT petid FROM pets)";
      executeNoParam(connection, sql);
   }

   public void deleteMissingPets(Connection connection) {
      String sql = "DELETE FROM pets WHERE petid NOT IN (SELECT petid FROM inventoryitems WHERE petid != -1)";
      executeNoParam(connection, sql);
   }

   public void deleteAllPetData(Connection connection, int petId) {
      PetAdministrator.getInstance().deletePet(connection, petId);
      PetIgnoreAdministrator.getInstance().deletePetIgnore(connection, petId);
   }

   public void deletePet(Connection connection, int petId) {
      String sql = "DELETE FROM pets WHERE `petid` = ?";
      execute(connection, sql, ps -> ps.setInt(1, petId));
   }

   public int createPet(Connection connection, int itemId, byte level, int closeness, int fullness) {
      int ret = MapleCashIdGenerator.getInstance().generateCashId();
      String sql = "INSERT INTO pets (petid, name, level, closeness, fullness, summoned, flag) VALUES (?, ?, ?, ?, ?, 0, 0)";
      execute(connection, sql, ps -> {
         ps.setInt(1, ret);
         ps.setString(2, MapleItemInformationProvider.getInstance().getName(itemId));
         ps.setByte(3, level);
         ps.setInt(4, closeness);
         ps.setInt(5, fullness);
      });
      return ret;
   }

   public void updatePet(Connection connection, String name, int level, int closeness, int fullness, boolean isSummoned, int petFlag, int petId) {
      String sql = "UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, summoned = ?, flag = ? WHERE petid = ?";
      execute(connection, sql, ps -> {
         ps.setString(1, name);
         ps.setInt(2, level);
         ps.setInt(3, closeness);
         ps.setInt(4, fullness);
         ps.setInt(5, isSummoned ? 1 : 0);
         ps.setInt(6, petFlag);
         ps.setInt(7, petId);
      });
   }
}