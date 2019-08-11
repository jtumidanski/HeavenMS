package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.SkillData;

public class SkillProvider extends AbstractQueryExecutor {
   private static SkillProvider instance;

   public static SkillProvider getInstance() {
      if (instance == null) {
         instance = new SkillProvider();
      }
      return instance;
   }

   private SkillProvider() {
   }

   public List<SkillData> getSkills(Connection connection, int characterId) {
      String sql = "SELECT skillid,skilllevel,masterlevel,expiration FROM skills WHERE characterid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, characterId),
            rs -> new SkillData(rs.getInt("skillid"), rs.getByte("skilllevel"), rs.getInt("masterlevel"), rs.getLong("expiration")));
   }
}