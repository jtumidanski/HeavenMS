package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.SkillMacroData;
import client.database.utility.SkillMacroTransformer;

public class SkillMacroProvider extends AbstractQueryExecutor {
   private static SkillMacroProvider instance;

   public static SkillMacroProvider getInstance() {
      if (instance == null) {
         instance = new SkillMacroProvider();
      }
      return instance;
   }

   private SkillMacroProvider() {
   }

   public List<SkillMacroData> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT * FROM skillmacros WHERE characterid = ?";
      SkillMacroTransformer transformer = new SkillMacroTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}