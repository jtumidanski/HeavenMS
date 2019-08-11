package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.NxCodeData;

public class NxCodeProvider extends AbstractQueryExecutor {
   private static NxCodeProvider instance;

   public static NxCodeProvider getInstance() {
      if (instance == null) {
         instance = new NxCodeProvider();
      }
      return instance;
   }

   private NxCodeProvider() {
   }

   public Optional<NxCodeData> get(Connection connection, String code) {
      String sql = "SELECT * FROM nxcode WHERE code = ?";
      return get(connection, sql, ps -> ps.setString(1, code), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(new NxCodeData(rs.getString("retriever"), rs.getLong("expiration"), rs.getInt("id")));
         }
         return Optional.empty();
      });
   }

   public List<Integer> getExpiredCodes(Connection connection, long time) {
      String sql = "SELECT * FROM nxcode WHERE expiration <= ?";
      return getListNew(connection, sql, ps -> ps.setLong(1, time), rs -> rs.getInt("id"));
   }
}