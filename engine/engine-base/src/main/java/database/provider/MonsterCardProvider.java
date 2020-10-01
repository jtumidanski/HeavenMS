package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.MonsterCardData;
import database.transformer.MonsterCardDataTransformer;

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
      TypedQuery<entity.MonsterCardData> query = entityManager.createQuery("SELECT m FROM MonsterCardData m", entity.MonsterCardData.class);
      return getResultList(query, new MonsterCardDataTransformer());
   }
}