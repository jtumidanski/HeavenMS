package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.TransferRockLocation;

public class TeleportRockLocationAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static TeleportRockLocationAdministrator instance;

   public static TeleportRockLocationAdministrator getInstance() {
      if (instance == null) {
         instance = new TeleportRockLocationAdministrator();
      }
      return instance;
   }

   private TeleportRockLocationAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM TransferRockLocation WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, List<Integer> mapIds, int vip) {
      List<TransferRockLocation> transferRockLocations = mapIds.stream().map(id -> {
         TransferRockLocation transferRockLocation = new TransferRockLocation();
         transferRockLocation.setCharacterId(characterId);
         transferRockLocation.setMapId(id);
         transferRockLocation.setVip(vip);
         return transferRockLocation;
      }).collect(Collectors.toList());
      insertBulk(entityManager, transferRockLocations);
   }
}