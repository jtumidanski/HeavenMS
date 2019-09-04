package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.QuestData;
import client.database.utility.QuestStatusTransformer;

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
      QuestStatusTransformer transformer = new QuestStatusTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}