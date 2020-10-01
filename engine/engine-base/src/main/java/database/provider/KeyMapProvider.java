package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.KeyMapData;
import database.transformer.KeyMapDataTransformer;
import entity.KeyMap;

public class KeyMapProvider extends AbstractQueryExecutor {
   private static KeyMapProvider instance;

   public static KeyMapProvider getInstance() {
      if (instance == null) {
         instance = new KeyMapProvider();
      }
      return instance;
   }

   private KeyMapProvider() {
   }

   public List<KeyMapData> getForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<KeyMap> query = entityManager.createQuery("SELECT k FROM KeyMap k WHERE k.characterId = :characterId", KeyMap.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new KeyMapDataTransformer());
   }
}