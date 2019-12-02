package database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import tools.Pair;

public class TeleportRockProvider extends AbstractQueryExecutor {
   private static TeleportRockProvider instance;

   public static TeleportRockProvider getInstance() {
      if (instance == null) {
         instance = new TeleportRockProvider();
      }
      return instance;
   }

   private TeleportRockProvider() {
   }

   public List<Pair<Integer, Integer>> getTeleportLocations(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("SELECT t.mapId, t.vip FROM TransferRockLocation t WHERE t.characterId = :characterId");
      query.setParameter("characterId", characterId);
      query.setMaxResults(15);
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((int) result[0], (int) result[1])).collect(Collectors.toList());
   }
}