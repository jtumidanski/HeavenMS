package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;

public class FamilyEntitlementProvider extends AbstractQueryExecutor {
   private static FamilyEntitlementProvider instance;

   public static FamilyEntitlementProvider getInstance() {
      if (instance == null) {
         instance = new FamilyEntitlementProvider();
      }
      return instance;
   }

   private FamilyEntitlementProvider() {
   }

   public List<Integer> getIdsByCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT f.entitlementId FROM FamilyEntitlement f WHERE f.characterId = :characterId", Integer.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}