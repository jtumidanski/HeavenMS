package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.Ring;
import client.database.AbstractQueryExecutor;
import client.database.utility.RingTransformer;

public class RingProvider extends AbstractQueryExecutor {
   private static RingProvider instance;

   public static RingProvider getInstance() {
      if (instance == null) {
         instance = new RingProvider();
      }
      return instance;
   }

   private RingProvider() {
   }

   public Optional<Ring> getRingById(Connection connection, int ringId) {
      String sql = "SELECT * FROM rings WHERE id = ?";
      RingTransformer transformer = new RingTransformer();
      return getNew(connection, sql, ps -> ps.setInt(1, ringId), transformer::transform);
   }

   public List<Integer> getAll(Connection connection) {
      String sql = "SELECT id FROM rings";
      return getListNew(connection, sql, rs -> rs.getInt("id"));
   }
}