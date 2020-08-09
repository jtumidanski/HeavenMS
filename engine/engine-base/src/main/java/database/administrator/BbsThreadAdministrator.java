package database.administrator;

import java.util.List;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import accessor.AbstractQueryExecutor;
import entity.bbs.BBSThread;
import net.server.Server;

public class BbsThreadAdministrator extends AbstractQueryExecutor {
   private static BbsThreadAdministrator instance;

   public static BbsThreadAdministrator getInstance() {
      if (instance == null) {
         instance = new BbsThreadAdministrator();
      }
      return instance;
   }

   private BbsThreadAdministrator() {
   }

   protected void update(EntityManager entityManager, int id, Consumer<BBSThread> consumer) {
      super.update(entityManager, BBSThread.class, id, consumer);
   }

   public void deleteThreadsFromCharacter(EntityManager entityManager, int characterId) {
      entityManager.getTransaction().begin();

      TypedQuery<Integer> threadQuery = entityManager.createQuery("SELECT b.threadId FROM BBSThread b WHERE b.posterId = :characterId", Integer.class);
      threadQuery.setParameter("characterId", characterId);
      List<Integer> threads = threadQuery.getResultList();

      if (threads.size() == 0) {
         entityManager.getTransaction().commit();
         return;
      }

      Query deleteReplyQuery = entityManager.createQuery("DELETE FROM BBSReply WHERE threadId in :threadIds");
      deleteReplyQuery.setParameter("threadIds", threads);
      deleteReplyQuery.executeUpdate();

      Query deleteThreadQuery = entityManager.createQuery("DELETE FROM BBSThread b WHERE b.posterId = :characterId");
      deleteThreadQuery.setParameter("characterId", characterId);
      deleteThreadQuery.executeUpdate();

      entityManager.getTransaction().commit();
   }

   public void decrementReplyCount(EntityManager entityManager, int threadId) {
      Query query = entityManager.createQuery("UPDATE BBSThread SET replyCount = replyCount - 1 WHERE threadId = :threadId");
      query.setParameter("threadId", threadId);
      execute(entityManager, query);
   }

   public void incrementReplyCount(EntityManager entityManager, int threadId) {
      Query query = entityManager.createQuery("UPDATE BBSThread SET replyCount = replyCount + 1 WHERE threadId = :threadId");
      query.setParameter("threadId", threadId);
      execute(entityManager, query);
   }

   public void deleteById(EntityManager entityManager, int threadId) {
      Query query = entityManager.createQuery("DELETE FROM BBSThread WHERE threadId = :threadId");
      query.setParameter("threadId", threadId);
      execute(entityManager, query);
   }

   public void editThread(EntityManager entityManager, int threadId, int guildId, int posterId, boolean privileged, String title, int icon, String text) {
      Query query = entityManager.createQuery(
            "UPDATE BBSThread SET name = :name, timestamp = :timestamp, icon = :icon, startPost = :startPost " +
                  "WHERE guildId = :guildId AND localThreadId = :localThreadId AND (posterId = :posterId OR :privileged)");
      query.setParameter("name", title);
      query.setParameter("timestamp", Server.getInstance().getCurrentTime());
      query.setParameter("icon", icon);
      query.setParameter("startPost", text);
      query.setParameter("guildId", guildId);
      query.setParameter("localThreadId", threadId);
      query.setParameter("posterId", posterId);
      query.setParameter("privileged", privileged);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int posterId, String title, int icon, String text, int guildId, int threadId) {
      BBSThread bbsThread = new BBSThread();
      bbsThread.setPosterId(posterId);
      bbsThread.setName(title);
      bbsThread.setTimestamp(Server.getInstance().getCurrentTime());
      bbsThread.setIcon(icon);
      bbsThread.setStartPost(text);
      bbsThread.setGuildId(guildId);
      bbsThread.setLocalThreadId(threadId);
      insert(entityManager, bbsThread);
   }
}
