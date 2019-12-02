package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import database.AbstractQueryExecutor;
import client.database.data.GuildData;

public class GuildProvider extends AbstractQueryExecutor {
   private static GuildProvider instance;

   public static GuildProvider getInstance() {
      if (instance == null) {
         instance = new GuildProvider();
      }
      return instance;
   }

   private GuildProvider() {
   }

   public int getByName(EntityManager entityManager, String name) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT g.guildId FROM Guild g WHERE g.name = :name", Integer.class);
      query.setParameter("name", name);
      return getSingleWithDefault(query, -1);
   }

   public int getByLeader(EntityManager entityManager, int leaderId) {
      TypedQuery<Integer> query = entityManager.createQuery("SELECT g.guildId FROM Guild g WHERE g.leader = :leaderId", Integer.class);
      query.setParameter("leaderId", leaderId);
      return getSingleWithDefault(query, -1);
   }

   public Optional<GuildData> getGuildDataById(EntityManager entityManager, int guildId) {
      TypedQuery<GuildData> query = entityManager.createQuery("" +
            "SELECT NEW client.database.data.GuildData(g.name, g.gp, g.logo, g.logoColor, g.logoBackground, " +
            "g.logoBackgroundColor, g.capacity, g.rank1Title, g.rank2Title, g.rank3Title, g.rank4Title, g.rank5Title, " +
            "g.leader, g.notice, g.signature, g.allianceId) " +
            "FROM Guild g WHERE g.guildId = :guildId", GuildData.class);
      query.setParameter("guildId", guildId);
      return getSingleOptional(query);
   }

   public List<GuildData> getGuildRankData(EntityManager entityManager) {
      TypedQuery<GuildData> query = entityManager.createQuery(
            "SELECT NEW client.database.data.GuildData(g.name, g.gp, g.logoBackground, g.logoBackgroundColor, g.logo, g.logoColor) " +
                  "FROM Guild g " +
                  "ORDER BY g.gp DESC", GuildData.class);
      query.setMaxResults(50);
      return query.getResultList();
   }
}