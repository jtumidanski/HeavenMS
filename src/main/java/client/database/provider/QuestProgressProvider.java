package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.QuestProgressData;

public class QuestProgressProvider extends AbstractQueryExecutor {
   private static QuestProgressProvider instance;

   public static QuestProgressProvider getInstance() {
      if (instance == null) {
         instance = new QuestProgressProvider();
      }
      return instance;
   }

   private QuestProgressProvider() {
   }

   public List<QuestProgressData> getProgress(Connection connection, int characterId) {
      String sql = "SELECT * FROM questprogress WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new QuestProgressData(rs.getInt("queststatusid"), rs.getInt("progressid"), rs.getString("progress")));
   }
}