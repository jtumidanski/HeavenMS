package client.database.administrator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import client.Skill;
import client.SkillEntry;
import client.database.AbstractQueryExecutor;
import client.database.DeleteForCharacter;
import tools.DatabaseConnection;

public class SkillAdministrator extends AbstractQueryExecutor implements DeleteForCharacter {
   private static SkillAdministrator instance;

   public static SkillAdministrator getInstance() {
      if (instance == null) {
         instance = new SkillAdministrator();
      }
      return instance;
   }

   private SkillAdministrator() {
   }

   @Override
   public void deleteForCharacter(EntityManager entityManager, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Skill WHERE characterId = :characterId");
      query.setParameter("characterId", characterId);
      execute(entityManager, query);
   }

   public void deleteForSkillCharacter(EntityManager entityManager, int skillId, int characterId) {
      Query query = entityManager.createQuery("DELETE FROM Skill WHERE skillId = :skillId AND characterId = :characterId");
      query.setParameter("characterId", characterId);
      query.setParameter("skillId", skillId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int characterId, Set<Map.Entry<Skill, SkillEntry>> skills) {
      List<entity.Skill> skillList = skills.stream().map(data -> {
         entity.Skill skill = new entity.Skill();
         skill.setCharacterId(characterId);
         skill.setSkillId(data.getKey().getId());
         skill.setSkillLevel((int) data.getValue().skillLevel());
         skill.setMasterLevel(data.getValue().masterLevel());
         skill.setExpiration(data.getValue().expiration());
         return skill;
      }).collect(Collectors.toList());
      insertBulk(entityManager, skillList);
   }

   public void replace(EntityManager entityManager, int characterId, Set<Map.Entry<Skill, SkillEntry>> skills) {
      DatabaseConnection.getInstance().thing(entityManager, em -> {
         skills.forEach(skillEntry -> {
            entity.Skill skill;
            TypedQuery<entity.Skill> skillQuery = em.createQuery("FROM Skill WHERE characterId = :characterId AND skillId = :skillId", entity.Skill.class);
            skillQuery.setParameter("characterId", characterId);
            skillQuery.setParameter("skillId", skillEntry.getKey().getId());
            try {
               skill = skillQuery.getSingleResult();
            } catch (NoResultException exception) {
               skill = new entity.Skill();
               skill.setCharacterId(characterId);
               skill.setSkillId(skillEntry.getKey().getId());
            }

            skill.setSkillLevel((int) skillEntry.getValue().skillLevel());
            skill.setMasterLevel(skillEntry.getValue().masterLevel());
            skill.setExpiration(skillEntry.getValue().expiration());
            em.persist(skill);
         });
      });
   }
}