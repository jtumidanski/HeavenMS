package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.CoolDownData;
import client.database.utility.CoolDownTransformer;

public class CoolDownProvider extends AbstractQueryExecutor {
   private static CoolDownProvider instance;

   public static CoolDownProvider getInstance() {
      if (instance == null) {
         instance = new CoolDownProvider();
      }
      return instance;
   }

   private CoolDownProvider() {
   }

   public List<CoolDownData> getForCharacter(Connection connection, int characterId) {
      String sql = "SELECT SkillID,StartTime,length FROM cooldowns WHERE charid = ?";
      CoolDownTransformer transformer = new CoolDownTransformer();
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId), transformer::transform);
   }
}