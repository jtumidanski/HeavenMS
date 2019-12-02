package database.provider;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.FrederickStorageData;
import client.processor.npc.FredrickProcessor;

public class FredStorageProvider extends AbstractQueryExecutor {
   private static FredStorageProvider instance;

   public static FredStorageProvider getInstance() {
      if (instance == null) {
         instance = new FredStorageProvider();
      }
      return instance;
   }

   private FredStorageProvider() {
   }

   public int get(EntityManager entityManager, int characterId) {
      TypedQuery<Timestamp> query = entityManager.createQuery("SELECT f.timestamp FROM FredStorage f WHERE f.characterId = :characterId", Timestamp.class);
      query.setParameter("characterId", characterId);
      return FredrickProcessor.timestampElapsedDays(query.getSingleResult(), System.currentTimeMillis());
   }

   public List<FrederickStorageData> get(EntityManager entityManager) {
      TypedQuery<FrederickStorageData> query = entityManager.createQuery("" +
            "SELECT NEW client.database.data.FrederickStorageData(c.id, c.name, c.world, f.timestamp, f.dayNotes, c.lastLogoutTime) " +
            "FROM FredStorage f LEFT JOIN Character c ON c.id = f.characterId", FrederickStorageData.class);
      return query.getResultList();
   }
}