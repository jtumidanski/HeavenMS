package database.administrator;


import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import database.AbstractQueryExecutor;
import entity.Guild;

public class GuildAdministrator extends AbstractQueryExecutor {
   private static GuildAdministrator instance;

   public static GuildAdministrator getInstance() {
      if (instance == null) {
         instance = new GuildAdministrator();
      }
      return instance;
   }

   private GuildAdministrator() {
   }

   protected void update(EntityManager entityManager, int id, Consumer<Guild> consumer) {
      super.update(entityManager, Guild.class, id, consumer);
   }

   public void deleteGuild(EntityManager entityManager, int guildId) {
      Query query = entityManager.createQuery("DELETE FROM Guild WHERE guildId = :guildId");
      query.setParameter("guildId", guildId);
      execute(entityManager, query);
   }

   public void createGuild(EntityManager entityManager, int leaderId, String name) {
      Guild guild = new Guild();
      guild.setLeader(leaderId);
      guild.setName(name);
      guild.setSignature((int) System.currentTimeMillis());
      insert(entityManager, guild);
   }

   public void update(EntityManager entityManager, int gp, int logo, int logoColor, int logoBackground,
                      int logoBackgroundColor, String[] rankTitles, int capacity, String notice, int guildId) {
      update(entityManager, guildId, guild -> {
         guild.setGp(gp);
         guild.setLogo(logo);
         guild.setLogoColor(logoColor);
         guild.setLogoBackground(logoBackground);
         guild.setLogoBackgroundColor(logoBackgroundColor);
         guild.setRank1Title(rankTitles[0]);
         guild.setRank2Title(rankTitles[1]);
         guild.setRank3Title(rankTitles[2]);
         guild.setRank4Title(rankTitles[3]);
         guild.setRank5Title(rankTitles[4]);
         guild.setCapacity(capacity);
         guild.setNotice(notice);
      });
   }

   public void setAlliance(EntityManager entityManager, int guildId, int allianceId) {
      update(entityManager, guildId, guild -> guild.setAllianceId(allianceId));
   }
}