package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.maker.MakerRecipeData;

public class MakerRecipeAdministrator extends AbstractQueryExecutor {
   private static MakerRecipeAdministrator instance;

   public static MakerRecipeAdministrator getInstance() {
      if (instance == null) {
         instance = new MakerRecipeAdministrator();
      }
      return instance;
   }

   private MakerRecipeAdministrator() {
   }

   public void create(EntityManager entityManager, int itemId, int requiredItem, int count) {
      MakerRecipeData makerRecipeData = new MakerRecipeData();
      makerRecipeData.setItemId(itemId);
      makerRecipeData.setRequiredItem(requiredItem);
      makerRecipeData.setCount(count);
      insert(entityManager, makerRecipeData);
   }
}