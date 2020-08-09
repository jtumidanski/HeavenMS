package database.administrator;

import java.math.BigInteger;
import javax.persistence.EntityManager;

import accessor.AbstractQueryExecutor;
import entity.QuickSlotKeyMap;

public class QuickSlotKeyMapAdministrator extends AbstractQueryExecutor {
   private static QuickSlotKeyMapAdministrator instance;

   public static QuickSlotKeyMapAdministrator getInstance() {
      if (instance == null) {
         instance = new QuickSlotKeyMapAdministrator();
      }
      return instance;
   }

   private QuickSlotKeyMapAdministrator() {
   }

   public void create(EntityManager entityManager, int accountId, BigInteger keyMap) {
      QuickSlotKeyMap quickSlotKeyMap = new QuickSlotKeyMap();
      quickSlotKeyMap.setId(accountId);
      quickSlotKeyMap.setKeyMap(keyMap);
      insert(entityManager, quickSlotKeyMap);
   }

   public void update(EntityManager entityManager, int accountId, BigInteger keyMap) {
      update(entityManager, QuickSlotKeyMap.class, accountId, quickSlotKeyMap -> quickSlotKeyMap.setKeyMap(keyMap));
   }
}