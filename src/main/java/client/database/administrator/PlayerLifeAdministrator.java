package client.database.administrator;

import java.sql.Connection;

import client.database.AbstractQueryExecutor;

public class PlayerLifeAdministrator extends AbstractQueryExecutor {
   private static PlayerLifeAdministrator instance;

   public static PlayerLifeAdministrator getInstance() {
      if (instance == null) {
         instance = new PlayerLifeAdministrator();
      }
      return instance;
   }

   private PlayerLifeAdministrator() {
   }

   public void create(Connection connection, int lifeId, int f, int fh, int cy, int rx0, int rx1, String type,
                      int xpos, int ypos, int worldId, int mapId, int mobTime, int hide) {
      String sql = "INSERT INTO plife ( life, f, fh, cy, rx0, rx1, type, x, y, world, map, mobtime, hide ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
      execute(connection, sql, ps -> {
         ps.setInt(1, lifeId);
         ps.setInt(2, f);
         ps.setInt(3, fh);
         ps.setInt(4, ypos);
         ps.setInt(5, rx0);
         ps.setInt(6, rx1);
         ps.setString(7, type);
         ps.setInt(8, xpos);
         ps.setInt(9, ypos);
         ps.setInt(10, worldId);
         ps.setInt(11, mapId);
         ps.setInt(12, mobTime);
         ps.setInt(13, hide);
      });
   }
}