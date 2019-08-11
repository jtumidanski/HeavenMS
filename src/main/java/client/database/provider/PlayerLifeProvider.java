package client.database.provider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerLifeData;
import client.database.utility.PlayerLifeTransformer;
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
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, worldId);
         ps.setInt(2, mapId);
         ps.setString(3, type);
         ps.setInt(4, lifeId);
      }, this::processPlayerLifeGet);
   }

   public List<Pair<Integer, Pair<Integer, Integer>>> get(Connection connection, int worldId, int mapId, String type, int xLower, int xUpper, int yLower, int yUpper) {
      String sql = "SELECT * FROM plife WHERE world = ? AND map = ? AND type LIKE ? AND x >= ? AND x <= ? AND y >= ? AND y <= ?";
      return getListNew(connection, sql, ps -> {
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
      PlayerLifeTransformer transformer = new PlayerLifeTransformer();
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, mapId);
         ps.setInt(2, worldId);
      }, transformer::transform);
   }

   private Pair<Integer, Pair<Integer, Integer>> processPlayerLifeGet(ResultSet rs) throws SQLException {
      return new Pair<>(rs.getInt("life"), new Pair<>(rs.getInt("x"), rs.getInt("y")));
   }
}