package client.database.administrator;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.MapleQuestStatus;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
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

   public int create(EntityManager entityManager, int characterId, MapleQuestStatus mapleQuestStatus) {
      QuestStatus questStatus = new QuestStatus();
      questStatus.setCharacterId(characterId);
      questStatus.setQuest((int) mapleQuestStatus.getQuest().getId());
      questStatus.setStatus(mapleQuestStatus.getStatus().getId());
      questStatus.setTime((int) (mapleQuestStatus.getCompletionTime() / 1000));
      questStatus.setExpires(mapleQuestStatus.getExpirationTime());
      questStatus.setForfeited(mapleQuestStatus.getForfeited());
      questStatus.setCompleted(mapleQuestStatus.getCompleted());
      insert(entityManager, questStatus);
      return questStatus.getQuestStatusId();
   }
}
