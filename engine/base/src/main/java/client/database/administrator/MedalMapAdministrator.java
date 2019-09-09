package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class MedalMapAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MedalMapAdministrator instance;

   public static MedalMapAdministrator getInstance() {
      if (instance == null) {
         instance = new MedalMapAdministrator();
      }
      return instance;
   }

   private MedalMapAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM medalmaps WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, int questId, List<Integer> medalMaps) {
      String sql = "INSERT INTO medalmaps VALUES (DEFAULT, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, questId);
         ps.setInt(3, data);
      }, medalMaps);
   }
}
