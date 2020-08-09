package database.administrator;

import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.DropDataGlobal;

public class DropDataGlobalAdministrator extends AbstractQueryExecutor {
   private static DropDataGlobalAdministrator instance;

   public static DropDataGlobalAdministrator getInstance() {
      if (instance == null) {
         instance = new DropDataGlobalAdministrator();
      }
      return instance;
   }

   private DropDataGlobalAdministrator() {
   }

   public int create(EntityManager entityManager, int continent, int itemId, int minimumQuantity, int maximumQuantity, int questId, int chance, String comment) {
      DropDataGlobal dropDataGlobal = new DropDataGlobal();
      dropDataGlobal.setContinent(continent);
      dropDataGlobal.setItemId(itemId);
      dropDataGlobal.setMinimumQuantity(minimumQuantity);
      dropDataGlobal.setMaximumQuantity(maximumQuantity);
      dropDataGlobal.setQuestId(questId);
      dropDataGlobal.setChance(chance);
      dropDataGlobal.setComments(comment);
      insert(entityManager, dropDataGlobal);
      return dropDataGlobal.getId();
   }
}