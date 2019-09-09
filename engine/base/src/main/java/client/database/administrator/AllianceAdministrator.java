package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class AllianceAdministrator extends AbstractQueryExecutor {
   private static AllianceAdministrator instance;

   public static AllianceAdministrator getInstance() {
      if (instance == null) {
         instance = new AllianceAdministrator();
      }
      return instance;
   }

   private AllianceAdministrator() {
   }

   public int createAlliance(Connection connection, String name) {
      String sql = "INSERT INTO `alliance` (`name`) VALUES (?)";
      return insertAndReturnKey(connection, sql, ps -> ps.setString(1, name));
   }

   public void deleteAlliance(Connection connection, int allianceId) {
      String sql = "DELETE FROM `alliance` WHERE id = ?";
      execute(connection, sql, ps -> ps.setInt(1, allianceId));
   }

   public void updateAlliance(Connection connection, int allianceId, int capacity, String notice, String rank1, String rank2, String rank3, String rank4, String rank5) {
      String sql = "UPDATE `alliance` SET capacity = ?, notice = ?, rank1 = ?, rank2 = ?, rank3 = ?, rank4 = ?, rank5 = ? WHERE id = ?";
      execute(connection, sql, ps -> {
         ps.setInt(1, capacity);
         ps.setString(2, notice);
         ps.setString(3, rank1);
         ps.setString(4, rank2);
         ps.setString(5, rank3);
         ps.setString(6, rank4);
         ps.setString(7, rank5);
         ps.setInt(8, allianceId);
      });
   }
}