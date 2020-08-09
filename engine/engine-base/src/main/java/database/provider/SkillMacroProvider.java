package database.provider;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.SkillMacroData;
import client.database.utility.SkillMacroTransformer;
import entity.SkillMacro;

public class SkillMacroProvider extends AbstractQueryExecutor {
   private static SkillMacroProvider instance;

   public static SkillMacroProvider getInstance() {
      if (instance == null) {
         instance = new SkillMacroProvider();
      }
      return instance;
   }

   private SkillMacroProvider() {
   }

   public List<SkillMacroData> getForCharacter(EntityManager entityManager, int characterId) {
      SkillMacroTransformer transformer = new SkillMacroTransformer();
      TypedQuery<SkillMacro> query = entityManager.createQuery("FROM SkillMacro s WHERE s.characterId = :characterId", SkillMacro.class);
      query.setParameter("characterId", characterId);
      return query.getResultStream().map(transformer::transform).collect(Collectors.toList());
   }
}