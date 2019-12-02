package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.Buddy;

public class BuddyAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static BuddyAdministrator instance;

   public static BuddyAdministrator getInstance() {
      if (instance == null) {
         instance = new BuddyAdministrator();
      }
      return instance;
   }

   private BuddyAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteByBuddy(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE buddyId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteBuddy(EntityManager entityManager, int characterId, int buddyId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE characterId = :characterId AND buddyId = :buddyId");
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      execute(entityManager, query);
   }

   public void addBuddy(EntityManager entityManager, int characterId, int buddyId, String group, Boolean responseRequired) {
      Buddy buddy = new Buddy();
      buddy.setCharacterId(characterId);
      buddy.setBuddyId(buddyId);
      buddy.setBuddyGroup(group);
      buddy.setPending(1);
      buddy.setResponseRequired(responseRequired);
      insert(entityManager, buddy);
   }

   public void updateBuddy(EntityManager entityManager, int characterId, int buddyId, String group) {
      Query query = entityManager.createQuery("UPDATE Buddy SET buddyGroup = :group WHERE characterId = :characterId AND buddyId = :buddyId");
      query.setParameter("group", group);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      execute(entityManager, query);
   }

   public void updateBuddy(EntityManager entityManager, int characterId, int buddyId, int pending, Boolean responseRequired) {
      Query query = entityManager.createQuery("UPDATE Buddy SET pending = :pending, responseRequired = :responseRequired WHERE characterId = :characterId AND buddyId = :buddyId");
      query.setParameter("pending", pending);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      query.setParameter("responseRequired", responseRequired);
      execute(entityManager, query);
   }
}
