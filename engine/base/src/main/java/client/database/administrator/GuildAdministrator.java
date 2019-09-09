package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class GuildAdministrator extends AbstractQueryExecutor {
   private static GuildAdministrator instance;

   public static GuildAdministrator getInstance() {
      if (instance == null) {
         instance = new GuildAdministrator();
      }
      return instance;
   }

   private GuildAdministrator() {
   }

   public void deleteGuild(Connection connection, int guildId) {
      String sql = "DELETE FROM guilds WHERE guildid = ?";
      execute(connection, sql, ps -> ps.setInt(1, guildId));
   }

   public void createGuild(Connection connection, int leaderId, String name) {
      String sql = "INSERT INTO guilds (`leader`, `name`, `signature`) VALUES (?, ?, ?)";
      execute(connection, sql, ps -> {
         ps.setInt(1, leaderId);
         ps.setString(2, name);
         ps.setInt(3, (int) System.currentTimeMillis());
      });
   }

   public void update(Connection connection, int gp, int logo, int logoColor, int logoBackground,
                      int logoBackgroundColor, String[] rankTitles, int capacity, String notice, int guildId) {
      StringBuilder builder = new StringBuilder();
      builder.append("UPDATE guilds SET GP = ?, logo = ?, logoColor = ?, logoBG = ?, logoBGColor = ?, ");
      for (int i = 0; i < 5; i++) {
         builder.append("rank").append(i + 1).append("title = ?, ");
      }
      builder.append("capacity = ?, notice = ? WHERE guildid = ?");

      execute(connection, builder.toString(), ps -> {
         ps.setInt(1, gp);
         ps.setInt(2, logo);
         ps.setInt(3, logoColor);
         ps.setInt(4, logoBackground);
         ps.setInt(5, logoBackgroundColor);
         for (int i = 6; i < 11; i++) {
            ps.setString(i, rankTitles[i - 6]);
         }
         ps.setInt(11, capacity);
         ps.setString(12, notice);
         ps.setInt(13, guildId);
         ps.execute();

      });
   }

   public void setAlliance(Connection connection, int guildId, int allianceId) {
      String sql = "UPDATE guilds SET allianceId = ? WHERE guildid = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, allianceId);
         ps.setInt(2, guildId);
      });
   }
}