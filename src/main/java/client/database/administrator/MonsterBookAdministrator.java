package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class MonsterBookAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MonsterBookAdministrator instance;

   public static MonsterBookAdministrator getInstance() {
      if (instance == null) {
         instance = new MonsterBookAdministrator();
      }
      return instance;
   }

   private MonsterBookAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM monsterbook WHERE charid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   //TODO - should this be a bulk insert? would it be more legible?
   public void save(Connection connection, int charId, Set<Map.Entry<Integer, Integer>> cardSet) {
      String sql = getSaveString(charId, cardSet);
      executeNoParam(connection, sql);
   }

   private static String getSaveString(Integer charid, Set<Map.Entry<Integer, Integer>> cardSet) {
      char[] save = new char[400000]; // 500 * 10 * 10 * 8
      int i = 0;

      i = saveStringConcat(save, i, "INSERT INTO monsterbook VALUES ");

      for (Map.Entry<Integer, Integer> all : cardSet) {   // assuming maxsize 500 unique cards
         i = saveStringConcat(save, i, "(");
         i = saveStringConcat(save, i, charid);  //10 chars
         i = saveStringConcat(save, i, ", ");
         i = saveStringConcat(save, i, all.getKey());  //10 chars
         i = saveStringConcat(save, i, ", ");
         i = saveStringConcat(save, i, all.getValue());  //1 char due to being 0 ~ 5
         i = saveStringConcat(save, i, "),");
      }

      return new String(save, 0, i - 1);
   }

   private static int saveStringConcat(char[] data, int pos, Integer i) {
      return saveStringConcat(data, pos, i.toString());
   }

   private static int saveStringConcat(char[] data, int pos, String s) {
      int len = s.length();
      for (int j = 0; j < len; j++) {
         data[pos + j] = s.charAt(j);
      }

      return pos + len;
   }
}
