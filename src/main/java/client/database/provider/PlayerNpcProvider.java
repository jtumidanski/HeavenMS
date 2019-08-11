package client.database.provider;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import client.database.AbstractQueryExecutor;
import client.database.utility.PlayerNpcFromResultSetTransformer;
import server.life.MaplePlayerNPC;
import tools.Pair;

public class PlayerNpcProvider extends AbstractQueryExecutor {
   private static PlayerNpcProvider instance;

   public static PlayerNpcProvider getInstance() {
      if (instance == null) {
         instance = new PlayerNpcProvider();
      }
      return instance;
   }

   private PlayerNpcProvider() {
   }

   public List<MaplePlayerNPC> getForMapAndWorld(Connection connection, int mapId, int worldId) {
      PlayerNpcFromResultSetTransformer resultSetTransformer = new PlayerNpcFromResultSetTransformer();
      String sql = "SELECT * FROM playernpcs WHERE map = ? AND world = ?";
      List<MaplePlayerNPC> results = getListNew(connection, sql, ps -> {
         ps.setInt(1, mapId);
         ps.setInt(2, worldId);
      }, resultSetTransformer::transform);

      results.parallelStream().forEach(npc ->
            PlayerNpcEquipProvider.getInstance().getEquips(connection, npc.getObjectId())
                  .forEach(data -> npc.getEquips().put(data.getLeft(), data.getRight())));

      return results;
   }

   public Optional<MaplePlayerNPC> getByScriptId(Connection connection, int scriptId) {
      PlayerNpcFromResultSetTransformer resultSetTransformer = new PlayerNpcFromResultSetTransformer();
      String sql = "SELECT * FROM playernpcs WHERE scriptid = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, scriptId), resultSetTransformer::transform);
   }

   public Optional<MaplePlayerNPC> getById(Connection connection, int npcId) {
      PlayerNpcFromResultSetTransformer resultSetTransformer = new PlayerNpcFromResultSetTransformer();
      String sql = "SELECT * FROM playernpcs WHERE id = ?";
      return getNew(connection, sql, ps -> ps.setInt(1, npcId), resultSetTransformer::transform);
   }

   public int getMaxRank(Connection connection) {
      String sql = "SELECT max(overallrank) FROM playernpcs";
      Optional<Integer> result = getSingle(connection, sql, 1);
      return result.orElse(0);
   }

   public List<Pair<Integer, Integer>> getMaxRankByWorld(Connection connection) {
      String sql = "SELECT world, max(worldrank) FROM playernpcs GROUP BY world ORDER BY world";
      return getListNew(connection, sql, rs -> new Pair<>(rs.getInt(1), rs.getInt(2)));
   }

   public List<Pair<Pair<Integer, Integer>, AtomicInteger>> getMaxRankByJobAndWorld(Connection connection) {
      String sql = "SELECT world, job, max(worldjobrank) FROM playernpcs GROUP BY world, job ORDER BY world, job";
      return getListNew(connection, sql, rs -> {
         Pair<Integer, Integer> worldJobPair = new Pair<>(rs.getInt(1), rs.getInt(2));
         return new Pair<>(worldJobPair, new AtomicInteger(rs.getInt(3) + 1));
      });
   }

   public List<Integer> getAvailableScripts(Connection connection, int lowerBound, int upperBound) {
      String sql = "SELECT scriptid FROM playernpcs WHERE scriptid >= ? AND scriptid < ? ORDER BY scriptid";
      return getListNew(connection, sql, ps -> {
         ps.setInt(1, lowerBound);
         ps.setInt(2, upperBound);
      }, rs -> rs.getInt(1));
   }

   public List<Pair<Integer, Integer>> getLikeNameAndMap(Connection connection, String name, Integer mapId) {
      String sql = "SELECT id, map FROM playernpcs WHERE name LIKE ?" + (mapId != null ? " AND map = ?" : "");
      return getListNew(connection, sql, ps -> {
         ps.setString(1, name);
         if (mapId != null) {
            ps.setInt(2, mapId);
         }
      }, rs -> new Pair<>(rs.getInt("id"), rs.getInt("map")));
   }

   public List<Pair<Integer, Integer>> getWorldMapsWithPlayerNpcs(Connection connection) {
      String sql = "SELECT DISTINCT world, map FROM playernpcs";
      return getListNew(connection, sql, rs -> new Pair<>(rs.getInt("world"), rs.getInt("map")));
   }
}