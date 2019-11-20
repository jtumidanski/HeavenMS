package client.database.administrator;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.inventory.manipulator.MapleCashIdGenerator;
import entity.Pet;
import server.MapleItemInformationProvider;

public class PetAdministrator extends AbstractQueryExecutor {
   private static PetAdministrator instance;

   public static PetAdministrator getInstance() {
      if (instance == null) {
         instance = new PetAdministrator();
      }
      return instance;
   }

   private PetAdministrator() {
   }

   public void unreferenceMissingPetsFromInventory(EntityManager entityManager) {
      entityManager.getTransaction().begin();

      TypedQuery<Integer> petIdQuery = entityManager.createQuery("SELECT p.petId FROM Pet p", Integer.class);
      List<Integer> petIds = petIdQuery.getResultList();

      if (petIds.size() > 0) {
         Query query = entityManager.createQuery("UPDATE InventoryItem SET petId = -1, expiration = 0 WHERE petId != -1 AND petId NOT IN :petIds");
         query.setParameter("petIds", petIds);
         execute(entityManager, query);
      }

      entityManager.getTransaction().commit();
   }

   public void deleteMissingPets(EntityManager entityManager) {
      entityManager.getTransaction().begin();

      TypedQuery<Integer> petIdQuery = entityManager.createQuery("SELECT DISTINCT p.petId FROM InventoryItem p WHERE p.petId != -1", Integer.class);
      List<Integer> petIds = petIdQuery.getResultList();

      if (petIds.size() > 0) {
         Query query = entityManager.createQuery("DELETE FROM Pet WHERE petId NOT IN :petIds");
         query.setParameter("petIds", petIds);
         execute(entityManager, query);
      }

      entityManager.getTransaction().commit();
   }

   public void deleteAllPetData(EntityManager entityManager, int petId) {
      PetAdministrator.getInstance().deletePet(entityManager, petId);
      PetIgnoreAdministrator.getInstance().deletePetIgnore(entityManager, petId);
   }

   public void deletePet(EntityManager entityManager, int petId) {
      Query query = entityManager.createQuery("DELETE FROM Pet WHERE petId = :petId");
      query.setParameter("petId", petId);
      execute(entityManager, query);
   }

   public int createPet(EntityManager entityManager, int itemId, byte level, int closeness, int fullness) {
      int ret = MapleCashIdGenerator.getInstance().generateCashId();
      Pet pet = new Pet();
      pet.setPetId(ret);
      pet.setName(MapleItemInformationProvider.getInstance().getName(itemId));
      pet.setLevel((int) level);
      pet.setCloseness(closeness);
      pet.setFullness(fullness);
      insert(entityManager, pet);
      return ret;
   }

   public void updatePet(EntityManager entityManager, String name, int level, int closeness, int fullness, boolean isSummoned, int petFlag, int petId) {
      Query query = entityManager.createQuery("UPDATE Pet SET name = :name, level = :level, closeness = :closeness, fullness = :fullness, summoned = :summoned, flag = :flag WHERE petId = :petId");
      query.setParameter("name", name);
      query.setParameter("level", level);
      query.setParameter("closeness", closeness);
      query.setParameter("fullness", fullness);
      query.setParameter("summoned", isSummoned ? 1 : 0);
      query.setParameter("flag", petFlag);
      query.setParameter("petId", petId);
      execute(entityManager, query);
   }
}