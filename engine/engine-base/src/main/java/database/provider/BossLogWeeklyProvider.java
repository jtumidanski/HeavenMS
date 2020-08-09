package database.provider;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import entity.boss.BossType;

public class BossLogWeeklyProvider extends AbstractQueryExecutor {
   private static BossLogWeeklyProvider instance;

   public static BossLogWeeklyProvider getInstance() {
      if (instance == null) {
         instance = new BossLogWeeklyProvider();
      }
      return instance;
   }

   private BossLogWeeklyProvider() {
   }

   public long countEntriesForCharacter(EntityManager entityManager, int characterId, String type) {
      TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(*) FROM BossLogWeekly b WHERE b.characterId = :characterId AND b.bossType = :bossType", Long.class);
      query.setParameter("characterId", characterId);
      query.setParameter("bossType", BossType.valueOf(type));
      return getSingleWithDefault(query, -1L);
   }
}