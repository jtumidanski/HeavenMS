package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.MonsterBookData;

public class MonsterBookProvider extends AbstractQueryExecutor {
   private static MonsterBookProvider instance;

   public static MonsterBookProvider getInstance() {
      if (instance == null) {
         instance = new MonsterBookProvider();
      }
      return instance;
   }

   private MonsterBookProvider() {
   }

   public List<MonsterBookData> getDataForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<MonsterBookData> query = entityManager.createQuery("SELECT NEW client.database.data.MonsterBookData(m.cardId, m.level) FROM MonsterBook m WHERE m.characterId = :characterId ORDER BY m.cardId ASC", MonsterBookData.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }

   public int[] getCardTierSize(EntityManager entityManager) {
      Query query = entityManager.createQuery("SELECT COUNT(*) FROM MonsterCardData m GROUP BY FLOOR(m.cardId / 1000)");
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().mapToInt(result -> (int) result[0]).toArray();
   }
}