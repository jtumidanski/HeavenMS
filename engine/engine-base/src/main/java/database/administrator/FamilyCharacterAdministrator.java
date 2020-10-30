package database.administrator;


import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.family.FamilyCharacter;

public class FamilyCharacterAdministrator extends AbstractQueryExecutor {
   private static FamilyCharacterAdministrator instance;

   public static FamilyCharacterAdministrator getInstance() {
      if (instance == null) {
         instance = new FamilyCharacterAdministrator();
      }
      return instance;
   }

   private FamilyCharacterAdministrator() {
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM FamilyCharacter WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   protected void update(EntityManager entityManager, int id, Consumer<FamilyCharacter> consumer) {
      super.update(entityManager, FamilyCharacter.class, id, consumer);
   }

   public void updatePrecepts(EntityManager entityManager, int characterId, String message) {
      update(entityManager, characterId, familyCharacter -> familyCharacter.setPrecepts(message));
   }

   public void updateMember(EntityManager entityManager, int characterId, int reputation, int todaysReputation, int totalReputation, int reputationToSenior) {
      update(entityManager, characterId, familyCharacter -> {
         familyCharacter.setReputation(reputation);
         familyCharacter.setTodaysRep(todaysReputation);
         familyCharacter.setTotalReputation(totalReputation);
         familyCharacter.setRepToSenior(reputationToSenior);
      });
   }

   public void changeFamily(EntityManager entityManager, int characterId, int familyId, int seniorId) {
      update(entityManager, characterId, familyCharacter -> {
         familyCharacter.setFamilyId(familyId);
         familyCharacter.setSeniorId(seniorId);
      });
   }

   public void setFamilyForCharacter(EntityManager entityManager, int characterId, int familyId) {
      update(entityManager, characterId, familyCharacter -> familyCharacter.setFamilyId(familyId));
   }

   public void create(EntityManager entityManager, int characterId, int familyId, int seniorId) {
      FamilyCharacter familyCharacter = new FamilyCharacter();
      familyCharacter.setCharacterId(characterId);
      familyCharacter.setFamilyId(familyId);
      familyCharacter.setSeniorId(seniorId);
      insert(entityManager, familyCharacter);
   }

   public void resetReputationOlderThan(EntityManager entityManager, long resetTime) {
      Query query = entityManager.createQuery("UPDATE FamilyCharacter SET todaysRep = 0, repToSenior = 0 WHERE lastResetTime <= :lastResetTime");
      query.setParameter("lastResetTime", resetTime);
      execute(entityManager, query);
   }
}