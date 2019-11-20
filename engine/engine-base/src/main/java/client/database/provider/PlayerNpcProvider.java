package client.database.provider;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.utility.PlayerNpcFromResultSetTransformer;
import entity.PlayerNpc;
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

   public List<MaplePlayerNPC> getForMapAndWorld(EntityManager entityManager, int mapId, int worldId) {
      PlayerNpcFromResultSetTransformer resultSetTransformer = new PlayerNpcFromResultSetTransformer();
      TypedQuery<PlayerNpc> query = entityManager.createQuery("FROM PlayerNpc p WHERE p.map = :mapId AND p.world = :worldId", PlayerNpc.class);
      query.setParameter("mapId", mapId);
      query.setParameter("worldId", worldId);

      List<MaplePlayerNPC> results = query.getResultStream().map(resultSetTransformer::transform).collect(Collectors.toList());

      results.parallelStream().forEach(npc ->
            PlayerNpcEquipProvider.getInstance().getEquips(entityManager, npc.objectId())
                  .forEach(data -> npc.getEquips().put(data.getLeft(), data.getRight())));

      return results;
   }

   public Optional<MaplePlayerNPC> getByScriptId(EntityManager entityManager, int scriptId) {
      TypedQuery<PlayerNpc> query = entityManager.createQuery("FROM PlayerNpc p WHERE p.scriptId = :scriptId", PlayerNpc.class);
      query.setParameter("scriptId", scriptId);
      return getSingleOptional(query, new PlayerNpcFromResultSetTransformer());
   }

   public Optional<MaplePlayerNPC> getById(EntityManager entityManager, int npcId) {
      TypedQuery<PlayerNpc> query = entityManager.createQuery("FROM PlayerNpc p WHERE p.id = :id", PlayerNpc.class);
      query.setParameter("id", npcId);
      return getSingleOptional(query, new PlayerNpcFromResultSetTransformer());
   }

   public int getMaxRank(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT COALESCE(MAX(p.overallRank), 0) FROM PlayerNpc p", Integer.class);
      return getSingleWithDefault(query, 0);
   }

   public List<Pair<Integer, Integer>> getMaxRankByWorld(EntityManager entityManager) {
      Query query = entityManager.createQuery("SELECT p.world, MAX(p.worldRank) FROM PlayerNpc p GROUP BY p.world ORDER BY p.world");
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (int) result[1])).collect(Collectors.toList());
   }

   public List<Pair<Pair<Integer, Integer>, AtomicInteger>> getMaxRankByJobAndWorld(EntityManager entityManager) {
      Query query = entityManager.createQuery("SELECT p.world, p.job, MAX(p.worldJobRank) FROM PlayerNpc p GROUP BY p.world, p.job ORDER BY p.world, p.job");
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> {
         Pair<Integer, Integer> worldJobPair = new Pair<>((int) result[0], (int) result[1]);
         return new Pair<>(worldJobPair, new AtomicInteger(((int) result[2]) + 1));
      }).collect(Collectors.toList());
   }

   public List<Integer> getAvailableScripts(EntityManager entityManager, int lowerBound, int upperBound) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT p.scriptId FROM PlayerNpc p WHERE p.scriptId >= :lowerBound AND p.scriptId < :upperBound ORDER BY p.scriptId", Integer.class);
      query.setParameter("lowerBound", lowerBound);
      query.setParameter("upperBound", upperBound);
      return query.getResultList();
   }

   public List<Pair<Integer, Integer>> getLikeNameAndMap(EntityManager entityManager, String name, Integer mapId) {
      Query query;
      if (mapId != null) {
         query = entityManager.createQuery("SELECT p.id, p.map FROM PlayerNpc p WHERE p.name LIKE :name AND p.map = :mapId");
         query.setParameter("name", name);
         query.setParameter("mapId", mapId);
      } else {
         query = entityManager.createQuery("SELECT p.id, p.map FROM PlayerNpc p WHERE p.name LIKE :name");
         query.setParameter("name", name);
      }
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (int) result[1])).collect(Collectors.toList());
   }

   public List<Pair<Integer, Integer>> getWorldMapsWithPlayerNpcs(EntityManager entityManager) {
      Query query = entityManager.createQuery("SELECT DISTINCT p.world, p.map FROM PlayerNpc p");
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (int) result[1])).collect(Collectors.toList());
   }
}