package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;
import client.database.data.QuestProgressData;
import client.database.utility.QuestProgressTransformer;
import entity.quest.QuestProgress;

public class QuestProgressProvider extends AbstractQueryExecutor {
   private static QuestProgressProvider instance;

   public static QuestProgressProvider getInstance() {
      if (instance == null) {
         instance = new QuestProgressProvider();
      }
      return instance;
   }

   private QuestProgressProvider() {
   }

   public List<QuestProgressData> getProgress(EntityManager entityManager, int characterId) {
      TypedQuery<QuestProgress> query = entityManager.createQuery("FROM QuestProgress p WHERE p.characterId = :characterId", QuestProgress.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new QuestProgressTransformer());
   }
}