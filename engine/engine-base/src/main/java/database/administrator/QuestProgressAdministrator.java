package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.quest.QuestProgress;
import tools.Pair;

public class QuestProgressAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static QuestProgressAdministrator instance;

   public static QuestProgressAdministrator getInstance() {
      if (instance == null) {
         instance = new QuestProgressAdministrator();
      }
      return instance;
   }

   private QuestProgressAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM QuestProgress WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, int questId, List<Pair<Integer, String>> progressData) {
      List<QuestProgress> questProgressList = progressData.stream().map(data -> {
         QuestProgress questProgress = new QuestProgress();
         questProgress.setCharacterId(characterId);
         questProgress.setQuestStatusId(questId);
         questProgress.setProgressId(data.getLeft());
         questProgress.setProgress(data.getRight());
         return questProgress;
      }).collect(Collectors.toList());
      insertBulk(entityManager, questProgressList);
   }
}
