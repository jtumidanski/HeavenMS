package client.database.administrator;

import java.sql.Connection;
import java.util.Set;

import client.database.AbstractQueryExecutor;

public class PetIgnoreAdministrator extends AbstractQueryExecutor {
   private static PetIgnoreAdministrator instance;

   public static PetIgnoreAdministrator getInstance() {
      if (instance == null) {
         instance = new PetIgnoreAdministrator();
      }
      return instance;
   }

   private PetIgnoreAdministrator() {
   }

   public void deletePetIgnore(Connection connection, int petId) {
      String sql = "DELETE FROM petignores WHERE `petid` = ?";
      execute(connection, sql, ps -> ps.setInt(1, petId));
   }

   public void create(Connection connection, int petId, Set<Integer> itemIds) {
      String sql = "INSERT INTO petignores (petid, itemid) VALUES (?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, petId);
         ps.setInt(2, data);
      }, itemIds);
   }
}