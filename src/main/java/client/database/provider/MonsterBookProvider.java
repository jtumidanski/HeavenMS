package client.database.provider;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.MonsterBookData;

public class MonsterBookProvider extends AbstractQueryExecutor {
   private static MonsterBookProvider instance;

   public static MonsterBookProvider getInstance() {
      if (instance == null) {
         instance = new MonsterBookProvider();
      }
      return instance;
   }

   private MonsterBookProvider() {
   }

   public List<MonsterBookData> getDataForCharacter(Connection connection, int characterId) {
      String sql = "SELECT cardid, level FROM monsterbook WHERE charid = ? ORDER BY cardid ASC";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new MonsterBookData(rs.getInt("cardid"), rs.getInt("level")));
   }
}