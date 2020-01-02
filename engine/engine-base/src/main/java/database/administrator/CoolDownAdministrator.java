package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import database.DeleteForCharacter;
import entity.CoolDown;
import net.server.PlayerCoolDownValueHolder;

public class CoolDownAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static CoolDownAdministrator instance;

   public static CoolDownAdministrator getInstance() {
      if (instance == null) {
         instance = new CoolDownAdministrator();
      }
      return instance;
   }

   private CoolDownAdministrator() {
   }

   public void addCoolDownsForCharacter(EntityManager entityManager, int characterId, List<PlayerCoolDownValueHolder> coolDowns) {
      List<CoolDown> coolDownList = coolDowns.stream().map(playerCoolDownValueHolder -> {
         CoolDown cooldown = new CoolDown();
         cooldown.setCharacterId(characterId);
         cooldown.setSkillId(playerCoolDownValueHolder.skillId);
         cooldown.setStartTime(playerCoolDownValueHolder.startTime);
         cooldown.setLength(playerCoolDownValueHolder.length);
         return cooldown;
      }).collect(Collectors.toList());
      insertBulk(entityManager, coolDownList);
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM CoolDown WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }
}
