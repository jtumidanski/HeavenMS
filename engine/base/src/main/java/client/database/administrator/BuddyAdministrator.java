package client.database.administrator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.BuddyListEntry;
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

   public void deleteNotPendingForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE characterId = :characterId AND pending = 0");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deletePendingForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE pending = 1 AND characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void addBuddy(EntityManager entityManager, int characterId, int buddyId) {
      Buddy buddy = new Buddy();
      buddy.setCharacterId(characterId);
      buddy.setBuddyId(buddyId);
      buddy.setPending(1);
      insert(entityManager, buddy);
   }

   public void addBuddies(EntityManager entityManager, int characterId, Collection<BuddyListEntry> buddies) {
      List<Buddy> buddyList = buddies.stream().map(buddyListEntry -> {
         Buddy buddy = new Buddy();
         buddy.setCharacterId(characterId);
         buddy.setBuddyId(buddyListEntry.characterId());
         buddy.setPending(0);
         buddy.setBuddyGroup(buddyListEntry.group());
         return buddy;
      }).collect(Collectors.toList());
      insertBulk(entityManager, buddyList);
   }

   public void deleteForCharacterOrBuddyId(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Buddy WHERE characterId = :characterId OR buddyId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
