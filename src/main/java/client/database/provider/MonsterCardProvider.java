package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.MonsterCardData;

public class MonsterCardProvider extends AbstractQueryExecutor {
   private static MonsterCardProvider instance;

   public static MonsterCardProvider getInstance() {
      if (instance == null) {
         instance = new MonsterCardProvider();
      }
      return instance;
   }

   private MonsterCardProvider() {
   }

   public List<MonsterCardData> getMonsterCardData(Connection connection) {
      String sql = "SELECT cardid, mobid FROM monstercarddata";
      return getList(connection, sql, ps -> {}, rs -> {
         List<MonsterCardData> cardData = new ArrayList<>();
         while (rs != null && rs.next()) {
            cardData.add(new MonsterCardData(rs.getInt(1), rs.getInt(2)));
         }
         return cardData;
      });
   }
}