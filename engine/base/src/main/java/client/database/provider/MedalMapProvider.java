package client.database.provider;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import entity.MedalMap;
import tools.Pair;

public class MedalMapProvider extends AbstractQueryExecutor {
   private static MedalMapProvider instance;

   public static MedalMapProvider getInstance() {
      if (instance == null) {
         instance = new MedalMapProvider();
      }
      return instance;
   }

   private MedalMapProvider() {
   }

   public List<Pair<Integer, Integer>> get(EntityManager entityManager, int characterId) {
      TypedQuery<MedalMap> query = entityManager.createQuery("FROM MedalMap m WHERE m.characterId = :characterId", MedalMap.class);
      query.setParameter("characterId", characterId);
      return query.getResultStream()
            .map(result -> new Pair<>(result.getQuestStatusId(), result.getMapId()))
            .collect(Collectors.toList());
   }
}