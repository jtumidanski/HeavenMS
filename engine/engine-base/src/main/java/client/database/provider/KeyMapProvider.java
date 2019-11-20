package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.KeyMapData;

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
      TypedQuery<KeyMapData> query = entityManager.createQuery("SELECT NEW client.database.data.KeyMapData(k.key, k.type, k.action) FROM KeyMap k WHERE k.characterId = :characterId", KeyMapData.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}