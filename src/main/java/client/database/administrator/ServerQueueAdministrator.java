package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class ServerQueueAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static ServerQueueAdministrator instance;

   public static ServerQueueAdministrator getInstance() {
      if (instance == null) {
         instance = new ServerQueueAdministrator();
      }
      return instance;
   }

   private ServerQueueAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM server_queue WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}