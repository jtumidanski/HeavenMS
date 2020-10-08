package database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import client.MapleQuestStatus;
import client.database.data.QuestData;
import database.DeleteForCharacter;
import database.transformer.QuestStatusTransformer;
import entity.quest.QuestStatus;

public class QuestStatusAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static QuestStatusAdministrator instance;

   public static QuestStatusAdministrator getInstance() {
      if (instance == null) {
         instance = new QuestStatusAdministrator();
      }
      return instance;
   }

   private QuestStatusAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM QuestStatus WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public QuestData create(EntityManager entityManager, int characterId, MapleQuestStatus mapleQuestStatus) {
      QuestStatus questStatus = new QuestStatus();
      questStatus.setCharacterId(characterId);
      questStatus.setQuest((int) mapleQuestStatus.questId());
      questStatus.setStatus(mapleQuestStatus.status().getId());
      questStatus.setTime((int) (mapleQuestStatus.completionTime() / 1000));
      questStatus.setExpires(mapleQuestStatus.expirationTime());
      questStatus.setForfeited(mapleQuestStatus.forfeited());
      questStatus.setCompleted(mapleQuestStatus.completed());
      insert(entityManager, questStatus);
      return new QuestStatusTransformer().transform(questStatus);
   }

   public void update(EntityManager entityManager, int questStatusId, client.QuestStatus status,
                      long completionTime, long expirationTime, int forfeited, int completed) {
      update(entityManager, QuestStatus.class, questStatusId, questStatus -> {
         questStatus.setStatus(status.getId());
         questStatus.setTime((int) (completionTime / 1000));
         questStatus.setExpires(expirationTime);
         questStatus.setForfeited(forfeited);
         questStatus.setCompleted(completed);
      });
   }
}
