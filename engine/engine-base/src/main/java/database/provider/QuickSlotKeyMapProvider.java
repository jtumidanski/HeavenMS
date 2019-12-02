package database.provider;

import java.math.BigInteger;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;

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

   public Optional<BigInteger> getForAccount(EntityManager entityManager, int accountId) {
      TypedQuery<BigInteger> query = entityManager.createQuery("SELECT k.keyMap FROM QuickSlotKeyMap k WHERE k.id = :accountId", BigInteger.class);
      query.setParameter("accountId", accountId);
      try {
         return Optional.of(query.getSingleResult());
      } catch (NoResultException exception) {
         return Optional.empty();
      }
   }
}