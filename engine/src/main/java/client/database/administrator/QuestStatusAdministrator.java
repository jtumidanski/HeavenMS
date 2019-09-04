package client.database.administrator;

import java.sql.Connection;

import client.MapleQuestStatus;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class QuestStatusAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static QuestStatusAdministrator instance;

   public static QuestStatusAdministrator getInstance() {
      if (instance == null) {
         instance = new QuestStatusAdministrator();
      }
      return instance;
   }

   private QuestStatusAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM queststatus WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public int create(Connection connection, int characterId, MapleQuestStatus mapleQuestStatus) {
      String sql = "INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `expires`, `forfeited`, `completed`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?)";
      return insertAndReturnKey(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, mapleQuestStatus.getQuest().getId());
         ps.setInt(3, mapleQuestStatus.getStatus().getId());
         ps.setInt(4, (int) (mapleQuestStatus.getCompletionTime() / 1000));
         ps.setLong(5, mapleQuestStatus.getExpirationTime());
         ps.setInt(6, mapleQuestStatus.getForfeited());
         ps.setInt(7, mapleQuestStatus.getCompleted());
      });
   }
}
