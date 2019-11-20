package client.database.administrator;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import client.database.AbstractQueryExecutor;
import entity.AllianceGuild;

public class AllianceGuildAdministrator extends AbstractQueryExecutor {
   private static AllianceGuildAdministrator instance;

   public static AllianceGuildAdministrator getInstance() {
      if (instance == null) {
         instance = new AllianceGuildAdministrator();
      }
      return instance;
   }

   private AllianceGuildAdministrator() {
   }

   public void addGuilds(EntityManager entityManager, int allianceId, List<Integer> guildIds) {
      entityManager.getTransaction().begin();
      for (int guildId : guildIds) {
         AllianceGuild allianceGuild = new AllianceGuild();
         allianceGuild.setAllianceId(allianceId);
         allianceGuild.setGuildId(guildId);
         entityManager.persist(allianceGuild);
      }
      entityManager.getTransaction().commit();
   }

   public void deleteForAlliance(EntityManager entityManager, int allianceId) {
      Query query = entityManager.createQuery("DELETE FROM AllianceGuild WHERE allianceId = :allianceId");
      query.setParameter("allianceId", allianceId);
      execute(entityManager, query);
   }

   public void removeGuild(EntityManager entityManager, int guildId) {
      Query query = entityManager.createQuery("DELETE FROM AllianceGuild WHERE guildId = :guildId");
      query.setParameter("guildId", guildId);
      execute(entityManager, query);
   }
}