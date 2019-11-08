package client.database.provider;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import client.database.AbstractQueryExecutor;

public class AllianceGuildProvider extends AbstractQueryExecutor {
   private static AllianceGuildProvider instance;

   public static AllianceGuildProvider getInstance() {
      if (instance == null) {
         instance = new AllianceGuildProvider();
      }
      return instance;
   }

   private AllianceGuildProvider() {
   }

   public List<Integer> getGuildsForAlliance(EntityManager entityManager, int allianceId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT a.guildId FROM AllianceGuild a WHERE a.allianceId = :allianceId", Integer.class);
      query.setParameter("allianceId", allianceId);
      return query.getResultList();
   }
}