package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class PetIgnoreProvider extends AbstractQueryExecutor {
   private static PetIgnoreProvider instance;

   public static PetIgnoreProvider getInstance() {
      if (instance == null) {
         instance = new PetIgnoreProvider();
      }
      return instance;
   }

   private PetIgnoreProvider() {
   }

   public List<Integer> getIgnoresForPet(Connection connection, int petId) {
      String sql = "SELECT itemid FROM petignores WHERE petid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, petId), rs -> rs.getInt(1));
   }
}