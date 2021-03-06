package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import entity.TempData;

public class TempDataAdministrator extends AbstractQueryExecutor {
   private static TempDataAdministrator instance;

   public static TempDataAdministrator getInstance() {
      if (instance == null) {
         instance = new TempDataAdministrator();
      }
      return instance;
   }

   private TempDataAdministrator() {
   }

   public void create(EntityManager entityManager, int dropperId, int itemId, int minimumQuantity, int maximumQuantity, int questId, int chance) {
      TempData tempData = new TempData();
      tempData.setDropperId(dropperId);
      tempData.setItemId(itemId);
      tempData.setMinimumQuantity(minimumQuantity);
      tempData.setMaximumQuantity(maximumQuantity);
      tempData.setQuestId(questId);
      tempData.setChance(chance);

      try {
         insert(entityManager, tempData);
      } catch (Exception exception) {
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXCEPTION, "Exception inserting record with key " + tempData.getDropperId() + "-" + tempData.getItemId());
      }
   }

   public void delete(EntityManager entityManager, int dropperId, int itemId) {
      Query query = entityManager.createQuery("DELETE FROM TempData t WHERE t.dropperId = :dropperId AND t.itemId = :itemId");
      query.setParameter("dropperId", dropperId);
      query.setParameter("itemId", itemId);
      execute(entityManager, query);
   }
}