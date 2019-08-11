package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.MapleRing;
import client.database.AbstractQueryExecutor;

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

   public Optional<MapleRing> getRingById(Connection connection, int ringId) {
      String sql = "SELECT * FROM rings WHERE id = ?";
      return get(connection, sql, ps -> ps.setInt(1, ringId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new MapleRing(ringId, rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName")));
         }
         return Optional.empty();
      });
   }

   public List<Integer> getAll(Connection connection) {
      String sql = "SELECT id FROM rings";
      return getListNew(connection, sql, ps -> {
      }, rs -> rs.getInt("id"));
   }
}