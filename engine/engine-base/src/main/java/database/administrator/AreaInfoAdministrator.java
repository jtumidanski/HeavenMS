package database.administrator;

import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.AreaInfo;
import database.DatabaseConnection;

public class AreaInfoAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static AreaInfoAdministrator instance;

   public static AreaInfoAdministrator getInstance() {
      if (instance == null) {
         instance = new AreaInfoAdministrator();
      }
      return instance;
   }

   private AreaInfoAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM AreaInfo WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, Set<Map.Entry<Short, String>> areas) {
      DatabaseConnection.getInstance().thing(entityManager, em -> {
         for (Map.Entry<Short, String> entry : areas) {
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setCharacterId(characterId);
            areaInfo.setArea(entry.getKey().intValue());
            areaInfo.setInfo(entry.getValue());
            entityManager.persist(areaInfo);
         }
      });
   }
}
