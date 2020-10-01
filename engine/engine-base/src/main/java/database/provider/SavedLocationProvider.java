package database.provider;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.SavedLocationData;
import database.transformer.SavedLocationTransformer;
import entity.SavedLocation;

public class SavedLocationProvider extends AbstractQueryExecutor {
   private static SavedLocationProvider instance;

   public static SavedLocationProvider getInstance() {
      if (instance == null) {
         instance = new SavedLocationProvider();
      }
      return instance;
   }

   private SavedLocationProvider() {
   }

   public List<SavedLocationData> getForCharacter(EntityManager entityManager, int characterId) {
      SavedLocationTransformer transformer = new SavedLocationTransformer();
      TypedQuery<SavedLocation> query = entityManager.createQuery("FROM SavedLocation s WHERE s.characterId = :characterId", SavedLocation.class);
      query.setParameter("characterId", characterId);
      return query.getResultStream().map(transformer::transform).collect(Collectors.toList());
   }
}