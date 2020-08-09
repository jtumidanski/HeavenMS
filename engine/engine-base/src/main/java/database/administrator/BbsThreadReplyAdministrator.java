package database.administrator;


import javax.persistence.EntityManager;
import javax.persistence.Query;

import accessor.AbstractQueryExecutor;
import entity.bbs.BBSReply;
import net.server.Server;

public class BbsThreadReplyAdministrator extends AbstractQueryExecutor {
   private static BbsThreadReplyAdministrator instance;

   public static BbsThreadReplyAdministrator getInstance() {
      if (instance == null) {
         instance = new BbsThreadReplyAdministrator();
      }
      return instance;
   }

   private BbsThreadReplyAdministrator() {
   }

   public void deleteById(EntityManager entityManager, int replyId) {
      Query query = entityManager.createQuery("DELETE FROM BBSReply WHERE replyId = :replyId");
      query.setParameter("replyId", replyId);
      execute(entityManager, query);
   }

   public void deleteByThreadId(EntityManager entityManager, int threadId) {
      Query query = entityManager.createQuery("DELETE FROM BBSReply WHERE threadId = :threadId");
      query.setParameter("threadId", threadId);
      execute(entityManager, query);
   }

   public void create(EntityManager entityManager, int threadId, int playerId, String text) {
      BBSReply bbsReply = new BBSReply();
      bbsReply.setThreadId(threadId);
      bbsReply.setPosterId(playerId);
      bbsReply.setTimestamp(Server.getInstance().getCurrentTime());
      bbsReply.setContent(text);
      insert(entityManager, bbsReply);
   }
}