package database.administrator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.LocationType;
import server.maps.SavedLocation;
import tools.Pair;

public class SavedLocationAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static SavedLocationAdministrator instance;

   public static SavedLocationAdministrator getInstance() {
      if (instance == null) {
         instance = new SavedLocationAdministrator();
      }
      return instance;
   }

   private SavedLocationAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM SavedLocation WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, Collection<Pair<String, SavedLocation>> savedLocations) {
      List<entity.SavedLocation> savedLocationList = savedLocations.stream().map(location -> {
         entity.SavedLocation savedLocation = new entity.SavedLocation();
         savedLocation.setCharacterId(characterId);
         savedLocation.setLocationType(LocationType.valueOf(location.getLeft()));
         savedLocation.setMap(location.getRight().mapId());
         savedLocation.setPortal(location.getRight().portal());
         return savedLocation;
      }).collect(Collectors.toList());
      insertBulk(entityManager, savedLocationList);
   }
}