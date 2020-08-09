package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.FamilyData;
import client.database.utility.FamilyDataFromResultSetTransformer;
import entity.family.FamilyCharacter;

public class FamilyCharacterProvider extends AbstractQueryExecutor {
   private static FamilyCharacterProvider instance;

   public static FamilyCharacterProvider getInstance() {
      if (instance == null) {
         instance = new FamilyCharacterProvider();
      }
      return instance;
   }

   private FamilyCharacterProvider() {
   }

   public List<FamilyData> getAllFamilies(EntityManager entityManager) {
      TypedQuery<FamilyCharacter> query = entityManager.createQuery("FROM FamilyCharacter", FamilyCharacter.class);
      return getResultList(query, new FamilyDataFromResultSetTransformer());
   }
}