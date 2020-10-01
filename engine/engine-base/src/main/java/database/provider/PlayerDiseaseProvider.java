package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.PlayerDiseaseData;
import database.transformer.PlayerDiseaseTransformer;
import entity.PlayerDisease;

public class PlayerDiseaseProvider extends AbstractQueryExecutor {
   private static PlayerDiseaseProvider instance;

   public static PlayerDiseaseProvider getInstance() {
      if (instance == null) {
         instance = new PlayerDiseaseProvider();
      }
      return instance;
   }

   private PlayerDiseaseProvider() {
   }

   public List<PlayerDiseaseData> getForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<PlayerDisease> query = entityManager.createQuery("FROM PlayerDisease p WHERE p.characterId = :characterId", PlayerDisease.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new PlayerDiseaseTransformer());
   }
}