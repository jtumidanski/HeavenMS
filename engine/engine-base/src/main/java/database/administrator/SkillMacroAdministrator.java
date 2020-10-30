package database.administrator;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import net.server.SkillMacro;

public class SkillMacroAdministrator extends AbstractQueryExecutor {
   private static SkillMacroAdministrator instance;

   public static SkillMacroAdministrator getInstance() {
      if (instance == null) {
         instance = new SkillMacroAdministrator();
      }
      return instance;
   }

   private SkillMacroAdministrator() {
   }

   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM SkillMacro WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, List<SkillMacro> macros) {
      List<entity.SkillMacro> skillMacroList = macros.stream().map(macro -> {
         entity.SkillMacro skillMacro = new entity.SkillMacro();
         skillMacro.setCharacterId(characterId);
         skillMacro.setSkill1(macro.skill1());
         skillMacro.setSkill2(macro.skill2());
         skillMacro.setSkill3(macro.skill3());
         skillMacro.setName(macro.name());
         skillMacro.setShout(macro.shout());
         skillMacro.setPosition(macro.position());
         return skillMacro;
      }).collect(Collectors.toList());
      insertBulk(entityManager, skillMacroList);
   }
}