package client.database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import tools.Pair;

public class AreaInfoProvider extends AbstractQueryExecutor {
   private static AreaInfoProvider instance;

   public static AreaInfoProvider getInstance() {
      if (instance == null) {
         instance = new AreaInfoProvider();
      }
      return instance;
   }

   private AreaInfoProvider() {
   }

   public List<Pair<Short, String>> getAreaInfo(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("SELECT NEW tools.Pair(ai.area, ai.info) FROM AreaInfo ai WHERE ai.characterId = :characterId", Pair.class);
      query.setParameter("characterId", characterId);
      List<Object[]> list = (List<Object[]>) query.getResultList();
      return list.stream().map(result -> new Pair<>((short) result[0], (String) result[1])).collect(Collectors.toList());
   }
}