package client.database.provider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import entity.boss.BossType;

public class BossLogDailyProvider extends AbstractQueryExecutor {
   private static BossLogDailyProvider instance;

   public static BossLogDailyProvider getInstance() {
      if (instance == null) {
         instance = new BossLogDailyProvider();
      }
      return instance;
   }

   private BossLogDailyProvider() {
   }

   public long countEntriesForCharacter(EntityManager entityManager, int characterId, String type) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM BossLogDaily b WHERE b.characterId = :characterId AND b.bossType = :bossType", Long.class);
      query.setParameter("characterId", characterId);
      query.setParameter("bossType", BossType.valueOf(type));
      return getSingleWithDefault(query, -1L);
   }
}