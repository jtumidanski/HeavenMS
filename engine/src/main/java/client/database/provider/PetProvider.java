package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.PetData;
import client.database.utility.PetTransformer;

public class PetProvider extends AbstractQueryExecutor {
   private static PetProvider instance;

   public static PetProvider getInstance() {
      if (instance == null) {
         instance = new PetProvider();
      }
      return instance;
   }

   private PetProvider() {
   }

   public PetData loadPet(Connection connection, int petId) {
      String sql = "SELECT name, level, closeness, fullness, summoned, flag FROM pets WHERE petid = ?";
      PetTransformer transformer = new PetTransformer();
      Optional<PetData> result = getNew(connection, sql, ps -> ps.setInt(1, petId), transformer::transform);
      return result.orElse(null);
   }

   public List<Integer> getAll(Connection connection) {
      String sql = "SELECT petid FROM pets";
      return getListNew(connection, sql, ps -> {
      }, rs -> rs.getInt("petid"));
   }
}