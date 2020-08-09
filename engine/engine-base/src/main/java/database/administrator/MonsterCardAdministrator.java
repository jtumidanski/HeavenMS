package database.administrator;

import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.MonsterCardData;

public class MonsterCardAdministrator extends AbstractQueryExecutor {
   private static MonsterCardAdministrator instance;

   public static MonsterCardAdministrator getInstance() {
      if (instance == null) {
         instance = new MonsterCardAdministrator();
      }
      return instance;
   }

   private MonsterCardAdministrator() {
   }

   public int create(EntityManager entityManager, int cardId, int mobId) {
      MonsterCardData monsterCardData = new MonsterCardData();
      monsterCardData.setCardId(cardId);
      monsterCardData.setMobId(mobId);
      insert(entityManager, monsterCardData);
      return monsterCardData.getId();
   }
}