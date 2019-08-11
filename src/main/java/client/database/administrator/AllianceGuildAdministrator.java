package client.database.administrator;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class AllianceGuildAdministrator extends AbstractQueryExecutor {
   private static AllianceGuildAdministrator instance;

   public static AllianceGuildAdministrator getInstance() {
      if (instance == null) {
         instance = new AllianceGuildAdministrator();
      }
      return instance;
   }

   private AllianceGuildAdministrator() {
   }

   public void addGuilds(Connection connection, int allianceId, List<Integer> guildIds) {
      String sql = "INSERT INTO `allianceguilds` (`allianceid`, `guildid`) VALUES (?, ?)";
      batch(connection, sql, (ps, data) -> {
         ps.setInt(1, allianceId);
         ps.setInt(2, data);
      }, guildIds);
   }

   public void deleteForAlliance(Connection connection, int allianceId) {
      String sql = "DELETE FROM `allianceguilds` WHERE allianceid = ?";
      execute(connection, sql, ps -> ps.setInt(1, allianceId));
   }

   public void removeGuild(Connection connection, int guildId) {
      String sql = "DELETE FROM `allianceguilds` WHERE guildid = ?";
      execute(connection, sql, ps -> ps.setInt(1, guildId));
   }
}