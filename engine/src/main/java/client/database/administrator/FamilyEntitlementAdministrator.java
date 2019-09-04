package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class FamilyEntitlementAdministrator extends AbstractQueryExecutor {
   private static FamilyEntitlementAdministrator instance;

   public static FamilyEntitlementAdministrator getInstance() {
      if (instance == null) {
         instance = new FamilyEntitlementAdministrator();
      }
      return instance;
   }

   private FamilyEntitlementAdministrator() {
   }

   public void deleteByCharacterAndId(Connection connection, int characterId, int entitlementId) {
      String sql = "DELETE FROM family_entitlement WHERE entitlementid = ? AND charid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, entitlementId);
         ps.setInt(2, characterId);
      });
   }

   public void create(Connection connection, int entitlementId, int characterId) {
      String sql = "INSERT INTO family_entitlement (entitlementid, charid, timestamp) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, entitlementId);
         ps.setInt(2, characterId);
         ps.setLong(3, System.currentTimeMillis());
      });
   }

   public void deleteOlderThan(Connection connection, long time) {
      String sql = "DELETE FROM family_entitlement WHERE timestamp <= ?";
      execute(connection, sql, ps -> ps.setLong(1, time));
   }
}