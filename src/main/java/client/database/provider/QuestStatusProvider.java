package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.QuestData;

public class QuestStatusProvider extends AbstractQueryExecutor {
   private static QuestStatusProvider instance;

   public static QuestStatusProvider getInstance() {
      if (instance == null) {
         instance = new QuestStatusProvider();
      }
      return instance;
   }

   private QuestStatusProvider() {
   }

   public List<QuestData> getQuestData(Connection connection, int characterId) {
      String sql = "SELECT * FROM queststatus WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new QuestData(rs.getShort("quest"), rs.getInt("status"), rs.getLong("time"), rs.getLong("expires"), rs.getInt("forfeited"), rs.getInt("completed"), rs.getInt("queststatusid")));
   }
}