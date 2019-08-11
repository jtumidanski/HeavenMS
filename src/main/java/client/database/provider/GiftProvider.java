package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.GiftData;

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
      return getList(connection, sql, ps -> ps.setInt(1, characterId), rs -> {
         List<GiftData> giftData = new ArrayList<>();
         while (rs.next()) {
            giftData.add(new GiftData(rs.getInt("sn"), rs.getInt("ringid"), rs.getString("message"), rs.getString("from")));
         }
         return giftData;
      });
   }
}