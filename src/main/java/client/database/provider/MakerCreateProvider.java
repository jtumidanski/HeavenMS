package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.MakerCreateData;

public class MakerCreateProvider extends AbstractQueryExecutor {
   private static MakerCreateProvider instance;

   public static MakerCreateProvider getInstance() {
      if (instance == null) {
         instance = new MakerCreateProvider();
      }
      return instance;
   }

   private MakerCreateProvider() {
   }

   public Optional<MakerCreateData> getMakerCreateDataForItem(Connection connection, int itemId) {
      String sql = "SELECT req_level, req_maker_level, req_meso, quantity FROM makercreatedata WHERE itemid = ?";
      return get(connection, sql, ps -> ps.setInt(1, itemId), rs -> {
         if (rs != null && rs.next()) {
            return Optional.of(
                  new MakerCreateData(rs.getInt("req_level"), rs.getInt("req_maker_level"), rs.getInt("req_meso"), rs.getInt("quantity")));
         }
         return Optional.empty();
      });
   }
}