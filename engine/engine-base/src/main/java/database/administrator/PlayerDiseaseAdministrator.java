package database.administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.MapleDisease;
import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.PlayerDisease;
import server.life.MobSkill;
import tools.Pair;

public class PlayerDiseaseAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static PlayerDiseaseAdministrator instance;

   public static PlayerDiseaseAdministrator getInstance() {
      if (instance == null) {
         instance = new PlayerDiseaseAdministrator();
      }
      return instance;
   }

   private PlayerDiseaseAdministrator() {
   }

   public void addPlayerDiseasesForCharacter(EntityManager entityManager, int characterId, Set<Map.Entry<MapleDisease, Pair<Long, MobSkill>>> playerDiseases) {
      List<PlayerDisease> playerDiseaseList = playerDiseases.stream().map(disease -> {
         PlayerDisease playerDisease = new PlayerDisease();
         playerDisease.setCharacterId(characterId);
         playerDisease.setDisease(disease.getKey().ordinal());
         playerDisease.setMobSkillId(disease.getValue().getRight().skillId());
         playerDisease.setMobSkillLevel(disease.getValue().getRight().level());
         playerDisease.setLength(disease.getValue().getLeft().intValue());
         return playerDisease;
      }).collect(Collectors.toList());
      insertBulk(entityManager, playerDiseaseList);
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM PlayerDisease WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
