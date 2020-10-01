package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import database.transformer.MonsterDropEntryTransformer;
import database.transformer.MonsterGlobalDropEntryTransformer;
import entity.DropData;
import entity.DropDataGlobal;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;

public class DropDataProvider extends AbstractQueryExecutor {
   private static DropDataProvider instance;

   public static DropDataProvider getInstance() {
      if (instance == null) {
         instance = new DropDataProvider();
      }
      return instance;
   }

   private DropDataProvider() {
   }

   public List<Integer> getMonstersWhoDrop(EntityManager entityManager, int itemId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT d.dropperId FROM DropData d WHERE d.itemId = :itemId", Integer.class);
      query.setParameter("itemId", itemId);
      query.setMaxResults(50);
      return query.getResultList();
   }

   public List<MonsterDropEntry> getDropDataForMonster(EntityManager entityManager, int monsterId) {
      TypedQuery<DropData> query = entityManager.createQuery("SELECT d FROM DropData d WHERE d.dropperId = :dropperId", DropData.class);
      query.setParameter("dropperId", monsterId);
      return getResultList(query, new MonsterDropEntryTransformer());
   }

   public List<MonsterGlobalDropEntry> getGlobalDropData(EntityManager entityManager) {
      TypedQuery<DropDataGlobal> query = entityManager.createQuery("SELECT d FROM DropDataGlobal d WHERE d.chance > 0", DropDataGlobal.class);
      return getResultList(query, new MonsterGlobalDropEntryTransformer());
   }
}