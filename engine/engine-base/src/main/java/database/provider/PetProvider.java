package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.PetData;
import database.transformer.PetTransformer;
import entity.Pet;

public class PetProvider extends AbstractQueryExecutor {
   private static PetProvider instance;

   public static PetProvider getInstance() {
      if (instance == null) {
         instance = new PetProvider();
      }
      return instance;
   }

   private PetProvider() {
   }

   public PetData loadPet(EntityManager entityManager, int petId) {
      TypedQuery<Pet> query = entityManager.createQuery("SELECT p FROM Pet p WHERE p.petId = :petId", Pet.class);
      query.setParameter("petId", petId);
      return getSingleWithDefault(query, new PetTransformer(), null);
   }

   public List<Integer> getAll(EntityManager entityManager) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT p.petId FROM Pet p", Integer.class);
      return query.getResultList();
   }
}