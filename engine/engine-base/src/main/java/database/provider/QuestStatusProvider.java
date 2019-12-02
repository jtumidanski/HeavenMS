package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.QuestData;
import client.database.utility.QuestStatusTransformer;
import entity.quest.QuestStatus;

public class QuestStatusProvider extends AbstractQueryExecutor {
   private static QuestStatusProvider instance;

   public static QuestStatusProvider getInstance() {
      if (instance == null) {
         instance = new QuestStatusProvider();
      }
      return instance;
   }

   private QuestStatusProvider() {
   }

   public List<QuestData> getQuestData(EntityManager entityManager, int characterId) {
      TypedQuery<QuestStatus> query = entityManager.createQuery("FROM QuestStatus q WHERE q.characterId = :characterId", QuestStatus.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new QuestStatusTransformer());
   }
}