package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import net.server.SkillMacro;

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
      String sql = "DELETE FROM skillmacros WHERE characterid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }

   public void create(Connection connection, int characterId, List<SkillMacro> macros) {
      String sql = "INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.skill1());
         ps.setInt(3, data.skill2());
         ps.setInt(4, data.skill3());
         ps.setString(5, data.name());
         ps.setInt(6, data.shout());
         ps.setInt(7, data.position());
      }, macros);
   }
}