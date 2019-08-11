package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.SkillMacro;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;

public class SkillMacroAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static SkillMacroAdministrator instance;

   public static SkillMacroAdministrator getInstance() {
      if (instance == null) {
         instance = new SkillMacroAdministrator();
      }
      return instance;
   }

   private SkillMacroAdministrator() {
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM skillmacros WHERE characterid_to = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, List<SkillMacro> macros) {
      String sql = "INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getSkill1());
         ps.setInt(3, data.getSkill2());
         ps.setInt(4, data.getSkill3());
         ps.setString(5, data.getName());
         ps.setInt(6, data.getShout());
         ps.setInt(7, data.getPosition());
      }, macros);
   }
}