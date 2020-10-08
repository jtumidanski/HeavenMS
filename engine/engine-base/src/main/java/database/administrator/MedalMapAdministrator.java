package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.MedalMap;

public class MedalMapAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static MedalMapAdministrator instance;

   public static MedalMapAdministrator getInstance() {
      if (instance == null) {
         instance = new MedalMapAdministrator();
      }
      return instance;
   }

   private MedalMapAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM MedalMap WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteForQuest(EntityManager entityManager, int characterId, int questStatusId) {
      Query query = entityManager.createQuery("DELETE FROM MedalMap WHERE characterId = :characterId AND questStatusId = "
            + ":questStatusId");
      query.setParameter("characterId", characterId);
      query.setParameter("questStatusId", questStatusId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, int questId, List<Integer> medalMaps) {
      List<MedalMap> medalMapList = medalMaps.stream().map(mapId -> {
         MedalMap medalMap = new MedalMap();
         medalMap.setCharacterId(characterId);
         medalMap.setQuestStatusId(questId);
         medalMap.setMapId(mapId);
         return medalMap;
      }).collect(Collectors.toList());
      insertBulk(entityManager, medalMapList);
   }
}
