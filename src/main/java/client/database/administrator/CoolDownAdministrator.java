package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import net.server.PlayerCoolDownValueHolder;

public class CoolDownAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static CoolDownAdministrator instance;

   public static CoolDownAdministrator getInstance() {
      if (instance == null) {
         instance = new CoolDownAdministrator();
      }
      return instance;
   }

   private CoolDownAdministrator() {
   }

   public void addCoolDownsForCharacter(Connection connection, int characterId, List<PlayerCoolDownValueHolder> coolDowns) {
      String sql = "INSERT INTO cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.skillId);
         ps.setLong(3, data.startTime);
         ps.setLong(4, data.length);
      }, coolDowns);
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM cooldowns WHERE charid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}
