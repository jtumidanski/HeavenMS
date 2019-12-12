package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.maker.MakerCreateData;

public class MakerCreateAdministrator extends AbstractQueryExecutor {
   private static MakerCreateAdministrator instance;

   public static MakerCreateAdministrator getInstance() {
      if (instance == null) {
         instance = new MakerCreateAdministrator();
      }
      return instance;
   }

   private MakerCreateAdministrator() {
   }

   public void create(EntityManager entityManager, int id, int itemId, int requiredLevel, int requiredMakerLevel,
                      int requiredMeso, int requiredItem, int requiredEquip, int catalyst, int quantity, int tuc) {
      MakerCreateData makerCreateData = new MakerCreateData();
      makerCreateData.setId(id);
      makerCreateData.setItemId(itemId);
      makerCreateData.setRequiredLevel(requiredLevel);
      makerCreateData.setRequiredMakerLevel(requiredMakerLevel);
      makerCreateData.setRequiredMeso(requiredMeso);
      makerCreateData.setRequiredItem(requiredItem);
      makerCreateData.setRequiredEquip(requiredEquip);
      makerCreateData.setCatalyst(catalyst);
      makerCreateData.setQuantity(quantity);
      makerCreateData.setTuc(tuc);
      insert(entityManager, makerCreateData);
   }
}