package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.CoolDownData;
import database.transformer.CoolDownDataTransformer;
import entity.CoolDown;

public class CoolDownProvider extends AbstractQueryExecutor {
   private static CoolDownProvider instance;

   public static CoolDownProvider getInstance() {
      if (instance == null) {
         instance = new CoolDownProvider();
      }
      return instance;
   }

   private CoolDownProvider() {
   }

   public List<CoolDownData> getForCharacter(EntityManager entityManager, int characterId) {
      TypedQuery<CoolDown> query = entityManager.createQuery("SELECT c FROM CoolDown c WHERE c.characterId = :characterId", CoolDown.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new CoolDownDataTransformer());
   }
}