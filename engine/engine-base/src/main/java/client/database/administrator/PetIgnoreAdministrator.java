package client.database.administrator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.Pet;
import entity.PetIgnore;

public class PetIgnoreAdministrator extends AbstractQueryExecutor {
   private static PetIgnoreAdministrator instance;

   public static PetIgnoreAdministrator getInstance() {
      if (instance == null) {
         instance = new PetIgnoreAdministrator();
      }
      return instance;
   }

   private PetIgnoreAdministrator() {
   }

   public void deletePetIgnore(EntityManager entityManager, int petId) {
      Query query = entityManager.createQuery("DELETE FROM PetIgnore WHERE pet.id = :petId");
      query.setParameter("petId", petId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int petId, Set<Integer> itemIds) {
      List<PetIgnore> petIgnoreList = itemIds.stream().map(itemId -> {
         PetIgnore petIgnore = new PetIgnore();
         petIgnore.setPet(entityManager.getReference(Pet.class, petId));
         petIgnore.setItemId(itemId);
         return petIgnore;
      }).collect(Collectors.toList());
      insertBulk(entityManager, petIgnoreList);
   }
}