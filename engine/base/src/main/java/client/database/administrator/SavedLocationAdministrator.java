package client.database.administrator;

import java.sql.Connection;
import java.util.Collection;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import server.maps.SavedLocation;
import tools.Pair;

public class SavedLocationAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static SavedLocationAdministrator instance;

   public static SavedLocationAdministrator getInstance() {
      if (instance == null) {
         instance = new SavedLocationAdministrator();
      }
      return instance;
   }

   private SavedLocationAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM savedlocations WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, Collection<Pair<String, SavedLocation>> savedLocations) {
      String sql = "INSERT INTO savedlocations (characterid, `locationtype`, `map`, `portal`) VALUES (?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setString(2, data.getLeft());
         ps.setInt(3, data.getRight().mapId());
         ps.setInt(4, data.getRight().portal());
      }, savedLocations);
   }
}