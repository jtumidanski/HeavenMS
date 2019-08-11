package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.PetData;

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
      Optional<PetData> result = get(connection, sql, ps -> ps.setInt(1, petId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new PetData(
                  rs.getString("name"),
                  (byte) Math.min(rs.getByte("level"), 30),
                  Math.min(rs.getInt("closeness"), 30000),
                  Math.min(rs.getInt("fullness"), 100),
                  rs.getInt("summoned") == 1,
                  rs.getInt("flag")));
         }
         return Optional.empty();
      });
      return result.orElse(null);
   }

   public List<Integer> getAll(Connection connection) {
      String sql = "SELECT petid FROM pets";
      return getList(connection, sql, ps -> {}, rs -> {
         List<Integer> result = new ArrayList<>();
         while (rs != null && rs.next()) {
            result.add(rs.getInt("petid"));
         }
         return result;
      });
   }
}