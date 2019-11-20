package client.database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
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

   public void addBuddy(EntityManager entityManager, int characterId, int buddyId) {
      Buddy buddy = new Buddy();
      buddy.setCharacterId(characterId);
      buddy.setBuddyId(buddyId);
      buddy.setPending(1);
      insert(entityManager, buddy);
   }

   public void updateBuddy(EntityManager entityManager, int characterId, int buddyId, int pending) {
      Query query = entityManager.createQuery("UPDATE Buddy SET pending = :pending WHERE characterId = :characterId AND buddyId = :buddyId");
      query.setParameter("pending", pending);
      query.setParameter("characterId", characterId);
      query.setParameter("buddyId", buddyId);
      query.executeUpdate();
      execute(entityManager, query);
   }
}
