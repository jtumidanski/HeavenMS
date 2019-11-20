package client.database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class HwidAccountProvider extends AbstractQueryExecutor {
   private static HwidAccountProvider instance;

   public static HwidAccountProvider getInstance() {
      if (instance == null) {
         instance = new HwidAccountProvider();
      }
      return instance;
   }

   private HwidAccountProvider() {
   }

   public List<String> getHwidForAccount(EntityManager entityManager, int accountId) {
      TypedQuery<String> query = entityManager.createQuery("SELECT h.hwid FROM HwidAccount h WHERE h.accountId = :accountId", String.class);
      query.setParameter("accountId", accountId);
      return query.getResultList();
   }

   public List<Pair<String, Integer>> getForAccount(EntityManager entityManager, int accountId) {
      Query query = entityManager.createQuery("SELECT h.hwid, h.relevance FROM HwidAccount h WHERE h.accountId = :accountId");
      query.setParameter("accountId", accountId);
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().map(result -> new Pair<>((String) result[0], (int) result[1])).collect(Collectors.toList());
   }
}