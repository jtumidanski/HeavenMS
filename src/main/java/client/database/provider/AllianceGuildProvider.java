package client.database.provider;

import java.sql.Connection;
import java.util.List;

import client.database.AbstractQueryExecutor;

public class AllianceGuildProvider extends AbstractQueryExecutor {
   private static AllianceGuildProvider instance;

   public static AllianceGuildProvider getInstance() {
      if (instance == null) {
         instance = new AllianceGuildProvider();
      }
      return instance;
   }

   private AllianceGuildProvider() {
   }

   public List<Integer> getGuildsForAlliance(Connection connection, int allianceId) {
      String sql = "SELECT guildid FROM allianceguilds WHERE allianceid = ?";
      return getListNew(connection, sql, ps -> ps.setInt(1, allianceId), rs -> rs.getInt("guildid"));
   }
}