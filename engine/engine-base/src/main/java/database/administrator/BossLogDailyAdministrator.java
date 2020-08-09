package database.administrator;

import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.boss.BossLogDaily;
import entity.boss.BossType;

public class BossLogDailyAdministrator extends AbstractQueryExecutor {
   private static BossLogDailyAdministrator instance;

   public static BossLogDailyAdministrator getInstance() {
      if (instance == null) {
         instance = new BossLogDailyAdministrator();
      }
      return instance;
   }

   private BossLogDailyAdministrator() {
   }

   public void deleteByAttemptTimeAndBossType(EntityManager entityManager, Timestamp timestamp, String type) {
      Query query = entityManager.createQuery("DELETE FROM BossLogDaily WHERE attemptTime <= :timestamp AND bossType = :bossType");
      query.setParameter("timestamp", timestamp);
      query.setParameter("bossType", BossType.valueOf(type));
      execute(entityManager, query);
   }

   public void addAttempt(EntityManager entityManager, int characterId, String type) {
      BossLogDaily bossLogDaily = new BossLogDaily();
      bossLogDaily.setCharacterId(characterId);
      bossLogDaily.setBossType(BossType.valueOf(type));
      insert(entityManager, bossLogDaily);
   }
}