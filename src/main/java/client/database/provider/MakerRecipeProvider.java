package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.MakerRecipeData;

public class MakerRecipeProvider extends AbstractQueryExecutor {
   private static MakerRecipeProvider instance;

   public static MakerRecipeProvider getInstance() {
      if (instance == null) {
         instance = new MakerRecipeProvider();
      }
      return instance;
   }

   private MakerRecipeProvider() {
   }

   public List<MakerRecipeData> getRecipeForItem(Connection connection, int itemId) {
      String sql = "SELECT req_item, count FROM makerrecipedata WHERE itemid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, itemId),
            rs -> new MakerRecipeData(rs.getInt("req_item"), rs.getInt("count")));
   }

   public List<MakerRecipeData> getMakerDisassembledItems(Connection connection, int itemId) {
      String sql = "SELECT req_item, count FROM makerrecipedata WHERE itemid = ? AND req_item >= 4260000 AND req_item < 4270000";
      return getListNew(connection, sql, ps -> ps.setInt(1, itemId),
            rs -> new MakerRecipeData(rs.getInt("req_item"), rs.getInt("count") / 2));
   }

}