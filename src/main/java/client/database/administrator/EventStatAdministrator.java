package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import server.events.MapleEvents;

public class EventStatAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static EventStatAdministrator instance;

   public static EventStatAdministrator getInstance() {
      if (instance == null) {
         instance = new EventStatAdministrator();
      }
      return instance;
   }

   private EventStatAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM eventstats WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, Set<Map.Entry<String, MapleEvents>> events) {
      String sql = "INSERT INTO eventstats (characterid, name, info) VALUES (?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setString(2, data.getKey());
         ps.setInt(3, data.getValue().getInfo());
      }, events);
   }
}