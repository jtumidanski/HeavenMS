package database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.SkillData;
import client.database.utility.SkillTransformer;
import entity.Skill;

public class SkillProvider extends AbstractQueryExecutor {
   private static SkillProvider instance;

   public static SkillProvider getInstance() {
      if (instance == null) {
         instance = new SkillProvider();
      }
      return instance;
   }

   private SkillProvider() {
   }

   public List<SkillData> getSkills(EntityManager entityManager, int characterId) {
      TypedQuery<Skill> query = entityManager.createQuery("FROM Skill s WHERE s.characterId = :characterId", Skill.class);
      query.setParameter("characterId", characterId);
      return getResultList(query, new SkillTransformer());
   }
}