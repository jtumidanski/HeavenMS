package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class FameLogAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static FameLogAdministrator instance;

   public static FameLogAdministrator getInstance() {
      if (instance == null) {
         instance = new FameLogAdministrator();
      }
      return instance;
   }

   private FameLogAdministrator() {
   }

   public void addForCharacter(Connection connection, int fromId, int toId) {
      String sql = "INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, fromId);
         ps.setInt(2, toId);
      });
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM famelog WHERE characterid_to = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}
