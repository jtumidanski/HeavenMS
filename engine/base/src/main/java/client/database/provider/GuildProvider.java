package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import client.database.AbstractQueryExecutor;
import client.database.data.GuildData;

public class GuildProvider extends AbstractQueryExecutor {
   private static GuildProvider instance;

   public static GuildProvider getInstance() {
      if (instance == null) {
         instance = new GuildProvider();
      }
      return instance;
   }

   private GuildProvider() {
   }

   public int getByName(Connection connection, String name) {
      String sql = "SELECT guildid FROM guilds WHERE name = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setString(1, name), 1);
      return result.orElse(-1);
   }

   public int getByLeader(Connection connection, int leaderId) {
      String sql = "SELECT guildid FROM guilds WHERE leader = ?";
      Optional<Integer> result = getSingle(connection, sql, ps -> ps.setInt(1, leaderId), "guildid");
      return result.orElse(-1);
   }

   public Optional<GuildData> getGuildDataById(Connection connection, int guildId) {
      String sql = "SELECT * FROM guilds WHERE guildid = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, guildId), rs -> {
         String[] rankTitles = new String[5];
         for (int i = 1; i <= 5; i++) {
            rankTitles[i - 1] = rs.getString("rank" + i + "title");
         }

         return new GuildData(
               rs.getString("name"),
               rs.getInt("GP"),
               rs.getInt("logo"),
               rs.getInt("logoColor"),
               rs.getInt("logoBG"),
               rs.getInt("logoBGColor"),
               rs.getInt("capacity"),
               rankTitles,
               rs.getInt("leader"),
               rs.getString("notice"),
               rs.getInt("signature"),
               rs.getInt("allianceId")
         );
      });
   }

   public List<GuildData> getGuildRankData(Connection connection) {
      String sql = "SELECT `name`, `GP`, `logoBG`, `logoBGColor`, `logo`, `logoColor` FROM guilds ORDER BY `GP` DESC LIMIT 50";
      return getListNew(connection, sql, rs -> new GuildData(rs.getString("name"), rs.getInt("GP"),
            rs.getInt("logo"), rs.getInt("logoColor"), rs.getInt("logoBG"),
            rs.getInt("logoBGColor")));
   }
}