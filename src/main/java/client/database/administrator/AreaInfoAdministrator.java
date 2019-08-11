package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class AreaInfoAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static AreaInfoAdministrator instance;

   public static AreaInfoAdministrator getInstance() {
      if (instance == null) {
         instance = new AreaInfoAdministrator();
      }
      return instance;
   }

   private AreaInfoAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM area_info WHERE charid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, Set<Map.Entry<Short, String>> areas) {
      String sql = "INSERT INTO area_info (id, charid, area, info) VALUES (DEFAULT, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getKey());
         ps.setString(3, data.getValue());
      }, areas);
   }
}
