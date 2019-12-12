package database.administrator;

import javax.persistence.EntityManager;

import database.AbstractQueryExecutor;
import entity.ReactorDrop;

public class ReactorDropAdministrator extends AbstractQueryExecutor {
   private static ReactorDropAdministrator instance;

   public static ReactorDropAdministrator getInstance() {
      if (instance == null) {
         instance = new ReactorDropAdministrator();
      }
      return instance;
   }

   private ReactorDropAdministrator() {
   }

   public int create(EntityManager entityManager, int reactorId, int itemId, int chance, int questId) {
      ReactorDrop reactorDrop = new ReactorDrop();
      reactorDrop.setReactorId(reactorId);
      reactorDrop.setItemId(itemId);
      reactorDrop.setChance(chance);
      reactorDrop.setQuestId(questId);
      insert(entityManager, reactorDrop);
      return reactorDrop.getReactorDropId();
   }
}