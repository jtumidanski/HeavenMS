package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class TeleportRockLocationAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static TeleportRockLocationAdministrator instance;

   public static TeleportRockLocationAdministrator getInstance() {
      if (instance == null) {
         instance = new TeleportRockLocationAdministrator();
      }
      return instance;
   }

   private TeleportRockLocationAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM trocklocations WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, List<Integer> mapIds, int vip) {
      String sql = "INSERT INTO trocklocations(characterid, mapid, vip) VALUES (?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data);
         ps.setInt(3, vip);
      }, mapIds);
   }
}