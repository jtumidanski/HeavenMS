package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.KeyMapData;

public class KeyMapProvider extends AbstractQueryExecutor {
   private static KeyMapProvider instance;

   public static KeyMapProvider getInstance() {
      if (instance == null) {
         instance = new KeyMapProvider();
      }
      return instance;
   }

   private KeyMapProvider() {
   }

   public List<KeyMapData> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), rs -> new KeyMapData(rs.getInt("key"), rs.getInt("type"), rs.getInt("action")));
   }
}