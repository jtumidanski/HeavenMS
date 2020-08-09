package database.administrator;

import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.SpecialCashItem;

public class SpecialCashItemAdministrator extends AbstractQueryExecutor {
   private static SpecialCashItemAdministrator instance;

   public static SpecialCashItemAdministrator getInstance() {
      if (instance == null) {
         instance = new SpecialCashItemAdministrator();
      }
      return instance;
   }

   private SpecialCashItemAdministrator() {
   }

   public int create(EntityManager entityManager, int sn, int modifier, int info) {
      SpecialCashItem specialCashItem = new SpecialCashItem();
      specialCashItem.setSn(sn);
      specialCashItem.setModifier(modifier);
      specialCashItem.setInfo(info);
      insert(entityManager, specialCashItem);
      return specialCashItem.getId();
   }
}