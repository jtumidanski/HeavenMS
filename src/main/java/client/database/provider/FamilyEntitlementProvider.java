package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class FamilyEntitlementProvider extends AbstractQueryExecutor {
   private static FamilyEntitlementProvider instance;

   public static FamilyEntitlementProvider getInstance() {
      if (instance == null) {
         instance = new FamilyEntitlementProvider();
      }
      return instance;
   }

   private FamilyEntitlementProvider() {
   }

   public List<Integer> getIdsByCharacter(Connection connection, int characterId) {
      String sql = "SELECT entitlementid FROM family_entitlement WHERE charid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> rs.getInt("entitlementid"));
   }
}