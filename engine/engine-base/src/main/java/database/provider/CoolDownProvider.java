package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.CoolDownData;

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
      TypedQuery<CoolDownData> query = entityManager.createQuery("SELECT NEW client.database.data.CoolDownData(c.skillId, c.startTime, c.length) FROM Cooldown c WHERE c.characterId = :characterId", CoolDownData.class);
      query.setParameter("characterId", characterId);
      return query.getResultList();
   }
}