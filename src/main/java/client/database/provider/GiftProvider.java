package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.GiftData;
import client.database.utility.GiftDataTransformer;

public class GiftProvider extends AbstractQueryExecutor {
   private static GiftProvider instance;

   public static GiftProvider getInstance() {
      if (instance == null) {
         instance = new GiftProvider();
      }
      return instance;
   }

   private GiftProvider() {
   }

   public List<GiftData> getGiftsForCharacter(Connection connection, int characterId) {
      String sql = "SELECT * FROM `gifts` WHERE `to` = ?";
      GiftDataTransformer transformer = new GiftDataTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}