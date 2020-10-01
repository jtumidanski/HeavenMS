package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MonsterBookData;
import database.transformer.MonsterBookDataTransformer;
import entity.MonsterBook;

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
      TypedQuery<MonsterBook> query = entityManager.createQuery("SELECT m FROM MonsterBook m WHERE m.characterId = :characterId ORDER BY m.cardId ASC", MonsterBook.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new MonsterBookDataTransformer());
   }

   public int[] getCardTierSize(EntityManager entityManager) {
      Query query = entityManager.createQuery("SELECT COUNT(*) FROM MonsterCardData m GROUP BY FLOOR(m.cardId / 1000)");
      List<Object[]> results = (List<Object[]>) query.getResultList();
      return results.stream().mapToInt(result -> (int) result[0]).toArray();
   }
}