package client.database.administrator;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import client.MapleDisease;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import server.life.MobSkill;
import tools.Pair;

public class PlayerDiseaseAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static PlayerDiseaseAdministrator instance;

   public static PlayerDiseaseAdministrator getInstance() {
      if (instance == null) {
         instance = new PlayerDiseaseAdministrator();
      }
      return instance;
   }

   private PlayerDiseaseAdministrator() {
   }

   public void addPlayerDiseasesForCharacter(Connection connection, int characterId, Set<Map.Entry<MapleDisease, Pair<Long, MobSkill>>> playerDiseases) {
      String sql = "INSERT INTO playerdiseases (charid, disease, mobskillid, mobskilllv, length) VALUES (?, ?, ?, ?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, characterId);
         ps.setInt(2, data.getKey().ordinal());
         MobSkill ms = data.getValue().getRight();
         ps.setInt(3, ms.skillId());
         ps.setInt(4, ms.level());
         ps.setInt(5, data.getValue().getLeft().intValue());
      }, playerDiseases);
   }

   @Override
   public void deleteForCharacter(Connection connection, int characterId) {
      String sql = "DELETE FROM playerdiseases WHERE charid = ?";
      execute(connection, sql, ps -> ps.setInt(1, characterId));
   }
}
