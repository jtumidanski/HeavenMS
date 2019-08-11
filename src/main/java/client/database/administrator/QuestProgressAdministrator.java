package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import tools.Pair;

public class QuestProgressAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static QuestProgressAdministrator instance;

   public static QuestProgressAdministrator getInstance() {
      if (instance == null) {
         instance = new QuestProgressAdministrator();
      }
      return instance;
   }

   private QuestProgressAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM questprogress WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, int questId, List<Pair<Integer, String>> progessData) {
      String sql = "INSERT INTO questprogress VALUES (DEFAULT, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, questId);
         ps.setInt(3, data.getLeft());
         ps.setString(4, data.getRight());
      }, progessData);
   }
}
