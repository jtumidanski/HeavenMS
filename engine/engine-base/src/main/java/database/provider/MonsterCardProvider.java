package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MonsterCardData;

public class MonsterCardProvider extends AbstractQueryExecutor {
   private static MonsterCardProvider instance;

   public static MonsterCardProvider getInstance() {
      if (instance == null) {
         instance = new MonsterCardProvider();
      }
      return instance;
   }

   private MonsterCardProvider() {
   }

   public List<MonsterCardData> getMonsterCardData(EntityManager entityManager) {
      TypedQuery<MonsterCardData> query = entityManager.createQuery("SELECT NEW client.database.data.MonsterCardData(m.cardId, m.mobId) FROM MonsterCardData m", MonsterCardData.class);
      return query.getResultList();
   }
}