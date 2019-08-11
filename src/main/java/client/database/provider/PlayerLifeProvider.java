package client.database.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerLifeData;
import tools.Pair;

public class PlayerLifeProvider extends AbstractQueryExecutor {
   private static PlayerLifeProvider instance;

   public static PlayerLifeProvider getInstance() {
      if (instance == null) {
         instance = new PlayerLifeProvider();
      }
      return instance;
   }

   private PlayerLifeProvider() {
   }

   public List<Pair<Integer, Pair<Integer, Integer>>> get(Connection connection, int worldId, int mapId, String type, int lifeId) {
      String sql = "SELECT * FROM plife WHERE world = ? AND map = ? AND type LIKE ? AND life = ?";
      return getList(connection, sql, ps -> {
         ps.setInt(1, worldId);
         ps.setInt(2, mapId);
         ps.setString(3, type);
         ps.setInt(4, lifeId);
      }, this::processPlayerLifeGet);
   }

   public List<Pair<Integer, Pair<Integer, Integer>>> get(Connection connection, int worldId, int mapId, String type, int xLower, int xUpper, int yLower, int yUpper) {
      String sql = "SELECT * FROM plife WHERE world = ? AND map = ? AND type LIKE ? AND x >= ? AND x <= ? AND y >= ? AND y <= ?";
      return getList(connection, sql, ps -> {
         ps.setInt(1, worldId);
         ps.setInt(2, mapId);
         ps.setString(3, type);
         ps.setInt(4, xLower);
         ps.setInt(5, xUpper);
         ps.setInt(6, yLower);
         ps.setInt(7, yUpper);
      }, this::processPlayerLifeGet);
   }

   public List<PlayerLifeData> getForMapAndWorld(Connection connection, int mapId, int worldId) {
      String sql = "SELECT * FROM plife WHERE map = ? and world = ?";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, mapId);
         ps.setInt(2, worldId);
      }, rs -> new PlayerLifeData(
            rs.getInt("life"),
            rs.getString("type"),
            rs.getInt("cy"),
            rs.getInt("f"),
            rs.getInt("fh"),
            rs.getInt("rx0"),
            rs.getInt("rx1"),
            rs.getInt("x"),
            rs.getInt("y"),
            rs.getInt("hide"),
            rs.getInt("mobtime"),
            rs.getInt("team")
      ));
   }

   private List<Pair<Integer, Pair<Integer, Integer>>> processPlayerLifeGet(ResultSet rs) throws SQLException {
      List<Pair<Integer, Pair<Integer, Integer>>> results = new ArrayList<>();
      while (rs != null && rs.next()) {
         results.add(new Pair<>(rs.getInt("life"), new Pair<>(rs.getInt("x"), rs.getInt("y"))));
      }
      return results;
   }
}