package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
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
      TypedQuery<MonsterDropEntry> query = entityManager.createQuery(
            "SELECT NEW server.life.MonsterDropEntry(d.itemId, d.chance, d.minimumQuantity, d.maximumQuantity, d.questId) " +
                  "FROM DropData d " +
                  "WHERE d.dropperId = :dropperId", MonsterDropEntry.class);
      query.setParameter("dropperId", monsterId);
      return query.getResultList();
   }

   public List<MonsterGlobalDropEntry> getGlobalDropData(EntityManager entityManager) {
      TypedQuery<MonsterGlobalDropEntry> query = entityManager.createQuery(
            "SELECT NEW server.life.MonsterGlobalDropEntry(d.itemId, d.chance, d.continent, d.minimumQuantity, d.maximumQuantity, d.questId) " +
                  "FROM DropDataGlobal d " +
                  "WHERE d.chance > 0", MonsterGlobalDropEntry.class);
      return query.getResultList();
   }
}