package client.database.provider;

import java.sql.Connection;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.MakerReagentData;

public class MakerReagentProvider extends AbstractQueryExecutor {
   private static MakerReagentProvider instance;

   public static MakerReagentProvider getInstance() {
      if (instance == null) {
         instance = new MakerReagentProvider();
      }
      return instance;
   }

   private MakerReagentProvider() {
   }

   public Optional<MakerReagentData> getForItem(Connection connection, int itemId) {
      String sql = "SELECT stat, value FROM makerreagentdata WHERE itemid = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, itemId),
            rs -> new MakerReagentData(rs.getString("stat"), rs.getInt("value")));
   }
}