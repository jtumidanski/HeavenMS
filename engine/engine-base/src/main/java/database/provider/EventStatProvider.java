package database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import tools.Pair;

public class EventStatProvider extends AbstractQueryExecutor {
   private static EventStatProvider instance;

   public static EventStatProvider getInstance() {
      if (instance == null) {
         instance = new EventStatProvider();
      }
      return instance;
   }

   private EventStatProvider() {
   }

   public List<Pair<String, Integer>> getInfo(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("SELECT e.name, e.info FROM EventStat e WHERE e.characterId = :characterId");
      query.setParameter("characterId", characterId);
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((String) result[0], (int) result[1])).collect(Collectors.toList());
   }
}