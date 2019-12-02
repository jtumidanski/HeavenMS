package database.administrator;

import java.sql.Timestamp;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.boss.BossLogWeekly;
import entity.boss.BossType;

public class BossLogWeeklyAdministrator extends AbstractQueryExecutor {
   private static BossLogWeeklyAdministrator instance;

   public static BossLogWeeklyAdministrator getInstance() {
      if (instance == null) {
         instance = new BossLogWeeklyAdministrator();
      }
      return instance;
   }

   private BossLogWeeklyAdministrator() {
   }

   public void deleteByAttemptTimeAndBossType(EntityManager entityManager, Timestamp timestamp, String type) {
      Query query = entityManager.createQuery("DELETE FROM BossLogWeekly WHERE attemptTime <= :timestamp AND bossType = :bossType");
      query.setParameter("timestamp", timestamp);
      query.setParameter("bossType", BossType.valueOf(type));
      execute(entityManager, query);
   }

   public void addAttempt(EntityManager entityManager, int characterId, String type) {
      BossLogWeekly bossLogWeekly = new BossLogWeekly();
      bossLogWeekly.setCharacterId(characterId);
      bossLogWeekly.setBossType(BossType.valueOf(type));
      insert(entityManager, bossLogWeekly);
   }
}