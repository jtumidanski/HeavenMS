package client.database.provider;

import java.math.BigInteger;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;

public class QuickSlotKeyMapProvider extends AbstractQueryExecutor {
   private static QuickSlotKeyMapProvider instance;

   public static QuickSlotKeyMapProvider getInstance() {
      if (instance == null) {
         instance = new QuickSlotKeyMapProvider();
      }
      return instance;
   }

   private QuickSlotKeyMapProvider() {
   }

   public BigInteger getForAccount(EntityManager entityManager, int accountId) {
      TypedQuery<BigInteger> query = entityManager.createQuery("SELECT k.keyMap FROM QuickSlotKeyMap k WHERE k.id = :accountId", BigInteger.class);
      query.setParameter("accountId", accountId);
      return query.getSingleResult();
   }
}