package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.MapleKeyBinding;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class KeyMapAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static KeyMapAdministrator instance;

   public static KeyMapAdministrator getInstance() {
      if (instance == null) {
         instance = new KeyMapAdministrator();
      }
      return instance;
   }

   private KeyMapAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM keymap WHERE characterid_to = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, int key, int type, int action) {
      String sql = "INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, characterId);
         ps.setInt(2, key);
         ps.setInt(3, type);
         ps.setInt(4, action);
      });
   }

   public void create(Connection connection, int characterId, Set<Map.Entry<Integer, MapleKeyBinding>> bindings) {
      String sql = "INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getKey());
         ps.setInt(3, data.getValue().getType());
         ps.setInt(4, data.getValue().getAction());
      }, bindings);
   }
}