package client.database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.PlayerLifeData;
import client.database.utility.PlayerLifeTransformer;
import entity.PLife;
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

   public List<Pair<Integer, Pair<Integer, Integer>>> get(EntityManager entityManager, int worldId, int mapId, String type, int lifeId) {
      TypedQuery<PLife> query = entityManager.createQuery("FROM PLife p WHERE p.world = :world AND p.map = :map AND p.type LIKE :type AND p.life = :life", PLife.class);
      query.setParameter("world", worldId);
      query.setParameter("map", mapId);
      query.setParameter("type", type);
      query.setParameter("life", lifeId);
      return query.getResultStream().map(result -> new Pair<>(result.getLife(), new Pair<>(result.getX(), result.getY()))).collect(Collectors.toList());
   }

   public List<Pair<Integer, Pair<Integer, Integer>>> get(EntityManager entityManager, int worldId, int mapId, String type, int xLower, int xUpper, int yLower, int yUpper) {
      TypedQuery<PLife> query = entityManager.createQuery("FROM PLife p WHERE p.world = :world AND p.map = :map AND p.type LIKE :type AND p.x >= :xLower and p.x <= :xUpper AND p.y >= :yLower AND p.y <= :yUpper", PLife.class);
      query.setParameter("world", worldId);
      query.setParameter("map", mapId);
      query.setParameter("type", type);
      query.setParameter("xLower", xLower);
      query.setParameter("xUpper", xUpper);
      query.setParameter("yLower", yLower);
      query.setParameter("yUpper", yUpper);
      return query.getResultStream().map(result -> new Pair<>(result.getLife(), new Pair<>(result.getX(), result.getY()))).collect(Collectors.toList());
   }

   public List<PlayerLifeData> getForMapAndWorld(EntityManager entityManager, int mapId, int worldId) {
      TypedQuery<PLife> query = entityManager.createQuery("FROM PLife p WHERE p.world = :world AND p.map = :map", PLife.class);
      query.setParameter("world", worldId);
      query.setParameter("map", mapId);
      return getResultList(query, new PlayerLifeTransformer());
   }
}