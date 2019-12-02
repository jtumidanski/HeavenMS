package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.family.FamilyEntitlement;

public class FamilyEntitlementAdministrator extends AbstractQueryExecutor {
   private static FamilyEntitlementAdministrator instance;

   public static FamilyEntitlementAdministrator getInstance() {
      if (instance == null) {
         instance = new FamilyEntitlementAdministrator();
      }
      return instance;
   }

   private FamilyEntitlementAdministrator() {
   }

   public void deleteByCharacterAndId(EntityManager entityManager, int characterId, int entitlementId) {
      Query query = entityManager.createQuery("DELETE FROM FamilyEntitlement WHERE entitlementId = :entitlementId AND characterId = :characterId");
      query.setParameter("entitlementId", entitlementId);
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int entitlementId, int characterId) {
      FamilyEntitlement familyEntitlement = new FamilyEntitlement();
      familyEntitlement.setEntitlementId(entitlementId);
      familyEntitlement.setCharacterId(characterId);
      familyEntitlement.setTimestamp(System.currentTimeMillis());
      insert(entityManager, familyEntitlement);
   }

   public void deleteOlderThan(EntityManager entityManager, long time) {
      Query query = entityManager.createQuery("DELETE FROM FamilyEntitlement WHERE timestamp <= :time");
      query.setParameter("time", time);
      execute(entityManager, query);
   }
}