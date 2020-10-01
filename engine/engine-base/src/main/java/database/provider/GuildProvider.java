package database.provider;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import client.database.data.GuildData;
import client.database.data.GuildRankData;
import database.transformer.GuildDataTransformer;
import database.transformer.GuildRankDataTransformer;
import entity.Guild;

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
      TypedQuery<Guild> query = entityManager.createQuery("SELECT g FROM Guild g WHERE g.guildId = :guildId", Guild.class);
      query.setParameter("guildId", guildId);
      return getSingleOptional(query, new GuildDataTransformer());
   }

   public List<GuildRankData> getGuildRankData(EntityManager entityManager) {
      TypedQuery<Guild> query = entityManager.createQuery("SELECT g FROM Guild g ORDER BY g.gp DESC", Guild.class);
      query.setMaxResults(50);
      return getResultList(query, new GuildRankDataTransformer());
   }
}