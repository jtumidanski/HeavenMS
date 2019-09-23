package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.Skill;
import client.SkillEntry;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class SkillAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static SkillAdministrator instance;

   public static SkillAdministrator getInstance() {
      if (instance == null) {
         instance = new SkillAdministrator();
      }
      return instance;
   }

   private SkillAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM skills WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void deleteForSkillCharacter(Connection connection, int skillId, int characterId) {
      String sql = "DELETE FROM skills WHERE skillid = ? AND characterid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, skillId);
         ps.setInt(2, characterId);
      });
   }

   public void create(Connection connection, int characterId, Set<Map .Entry<Skill, SkillEntry>> skills) {
      String sql = "INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getKey().getId());
         ps.setInt(3, data.getValue().skillLevel());
         ps.setInt(4, data.getValue().masterLevel());
         ps.setLong(5, data.getValue().expiration());
      }, skills);
   }

   public void replace(Connection connection, int characterId, Set<Map .Entry<Skill, SkillEntry>> skills) {
      String sql = "REPLACE INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getKey().getId());
         ps.setInt(3, data.getValue().skillLevel());
         ps.setInt(4, data.getValue().masterLevel());
         ps.setLong(5, data.getValue().expiration());
      }, skills);
   }
}