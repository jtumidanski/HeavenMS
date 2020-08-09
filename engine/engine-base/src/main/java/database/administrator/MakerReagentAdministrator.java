package database.administrator;

import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.maker.MakerReagentData;

public class MakerReagentAdministrator extends AbstractQueryExecutor {
   private static MakerReagentAdministrator instance;

   public static MakerReagentAdministrator getInstance() {
      if (instance == null) {
         instance = new MakerReagentAdministrator();
      }
      return instance;
   }

   private MakerReagentAdministrator() {
   }

   public void create(EntityManager entityManager, int itemId, String stat, int value) {
      MakerReagentData makerReagentData = new MakerReagentData();
      makerReagentData.setItemId(itemId);
      makerReagentData.setStat(stat);
      makerReagentData.setValue(value);
      insert(entityManager, makerReagentData);
   }
}