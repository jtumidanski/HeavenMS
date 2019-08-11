package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.SkillMacroData;

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
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new SkillMacroData(rs.getInt("position"), rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout")));
   }
}